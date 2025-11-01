package ru.itmo.telecom.order.service;

import lombok.RequiredArgsConstructor;
import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.client.RestTemplate;
import ru.itmo.telecom.order.entity.*;
import ru.itmo.telecom.order.mapper.ApplicationMapper;
import ru.itmo.telecom.order.mapper.ApplicationDetailMapper;
import ru.itmo.telecom.order.repository.*;
import ru.itmo.telecom.shared.exceptions.ResourceNotFoundException;
import ru.itmo.telecom.shared.order.dto.ApplicationCreateRequest;
import ru.itmo.telecom.shared.order.dto.ApplicationDetailRequest;
import ru.itmo.telecom.shared.order.dto.ApplicationDto;
import ru.itmo.telecom.shared.tariff.dto.ServiceParameterDto;
import ru.itmo.telecom.shared.tariff.dto.TariffDto;
import ru.itmo.telecom.shared.utils.TelecomConstants;

import java.math.BigDecimal;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class ApplicationService {

    private final ApplicationRepository applicationRepository;
    private final ApplicationStatusRepository statusRepository;
    private final CustomApplicationRepository customApplicationRepository;
    private final TariffApplicationRepository tariffApplicationRepository;
    private final ApplicationDetailRepository applicationDetailRepository;
    private final ApplicationStatusHistoryRepository statusHistoryRepository;
    private final ApplicationMapper applicationMapper;
    private final ApplicationDetailMapper applicationDetailMapper;
    private final RestTemplate restTemplate;

    @Transactional
    public ApplicationDto createApplication(ApplicationCreateRequest createRequest) {
        // Получаем статус "СОЗДАНА"
        ApplicationStatus createdStatus = statusRepository.findByName(TelecomConstants.APP_STATUS_CREATED)
                .orElseThrow(() -> new ResourceNotFoundException("Статус заявки", TelecomConstants.APP_STATUS_CREATED));

        // Создаем основную заявку
        Application application = applicationMapper.toEntity(createRequest);
        application.setStatus(createdStatus);

        // Рассчитываем стоимость
        BigDecimal totalCost = calculateTotalCost(createRequest);
        application.setTotalCost(totalCost);

        // Сохраняем основную заявку
        Application savedApplication = applicationRepository.save(application);

        // Создаем запись в истории статусов
        ApplicationStatusHistory statusHistory = new ApplicationStatusHistory();
        statusHistory.setApplication(savedApplication);
        statusHistory.setStatus(createdStatus);
        statusHistory.setComment("Заявка создана через Telegram бота");
        statusHistoryRepository.save(statusHistory);

        // Обрабатываем тип заявки (готовый тариф или конструктор)
        if (createRequest.getTariffId() != null) {
            // Заявка на готовый тариф
            TariffApplication tariffApplication = new TariffApplication();
            tariffApplication.setId(savedApplication.getId());
            tariffApplication.setApplication(savedApplication);
            tariffApplication.setTariffId(createRequest.getTariffId());
            tariffApplicationRepository.save(tariffApplication);
        } else if (createRequest.getDetails() != null && !createRequest.getDetails().isEmpty()) {
            // Кастомная заявка (конструктор)
            CustomApplication customApplication = new CustomApplication();
            customApplication.setId(savedApplication.getId());
            customApplication.setApplication(savedApplication);
            customApplicationRepository.save(customApplication);

            // Сохраняем детали заявки
            saveApplicationDetails(savedApplication, createRequest.getDetails());
        }

        return applicationMapper.toDto(savedApplication);
    }

    private BigDecimal calculateTotalCost(ApplicationCreateRequest createRequest) {
        if (createRequest.getTariffId() != null) {
            // Расчет для готового тарифа
            return calculatePredefinedTariffCost(createRequest.getTariffId());
        } else if (createRequest.getDetails() != null && !createRequest.getDetails().isEmpty()) {
            // Расчет для конструктора
            return calculateCustomTariffCost(createRequest.getDetails());
        }
        return BigDecimal.ZERO;
    }

    private BigDecimal calculatePredefinedTariffCost(Integer tariffId) {
        try {
            // Вызываем tariff-service для получения информации о тарифе
            String tariffUrl = "http://localhost:8081/api/v1/tariffs/{id}";
            ResponseEntity<TariffDto> response = restTemplate.getForEntity(
                    tariffUrl, TariffDto.class, tariffId);

            if (response.getStatusCode().is2xxSuccessful() && response.getBody() != null) {
                TariffDto tariff = response.getBody();
                // В реальном приложении здесь была бы логика расчета стоимости готового тарифа
                // Для примера возвращаем фиксированную стоимость
                return BigDecimal.valueOf(500); // Примерная стоимость
            }
        } catch (Exception e) {
            // Логируем ошибку и возвращаем стоимость по умолчанию
            System.err.println("Ошибка при расчете стоимости тарифа: " + e.getMessage());
        }
        return BigDecimal.valueOf(500); // Стоимость по умолчанию
    }

    private BigDecimal calculateCustomTariffCost(List<ApplicationDetailRequest> details) {
        BigDecimal totalCost = BigDecimal.ZERO;

        for (ApplicationDetailRequest detail : details) {
            try {
                // Получаем информацию о параметре услуги
                String parameterUrl = "http://localhost:8081/api/v1/tariffs/parameters/{id}";
                ResponseEntity<ServiceParameterDto> response = restTemplate.getForEntity(
                        parameterUrl, ServiceParameterDto.class, detail.getParameterId());

                if (response.getStatusCode().is2xxSuccessful() && response.getBody() != null) {
                    ServiceParameterDto parameter = response.getBody();
                    BigDecimal parameterCost = parameter.getMaxPricePerUnit()
                            .multiply(BigDecimal.valueOf(detail.getVolume()));
                    totalCost = totalCost.add(parameterCost);
                }
            } catch (Exception e) {
                System.err.println("Ошибка при расчете стоимости параметра: " + e.getMessage());
            }
        }

        return totalCost;
    }

    private void saveApplicationDetails(Application application, List<ApplicationDetailRequest> details) {
        for (ApplicationDetailRequest detailRequest : details) {
            ApplicationDetail detail = new ApplicationDetail();
            ApplicationDetailId detailId = new ApplicationDetailId();
            detailId.setApplicationId(application.getId());
            detailId.setParameterId(detailRequest.getParameterId());

            detail.setId(detailId);
            detail.setApplication(application);
            detail.setVolume(detailRequest.getVolume());

            // Получаем актуальную цену за единицу
            BigDecimal unitPrice = getCurrentUnitPrice(detailRequest.getParameterId());
            detail.setUnitPrice(unitPrice);

            applicationDetailRepository.save(detail);
        }
    }

    private BigDecimal getCurrentUnitPrice(Integer parameterId) {
        try {
            String parameterUrl = "http://localhost:8081/api/v1/tariffs/parameters/{id}";
            ResponseEntity<ServiceParameterDto> response = restTemplate.getForEntity(
                    parameterUrl, ServiceParameterDto.class, parameterId);

            if (response.getStatusCode().is2xxSuccessful() && response.getBody() != null) {
                return response.getBody().getMaxPricePerUnit();
            }
        } catch (Exception e) {
            System.err.println("Ошибка при получении цены параметра: " + e.getMessage());
        }
        return BigDecimal.ZERO;
    }

    @Transactional(readOnly = true)
    public List<ApplicationDto> getApplicationsByClientId(Integer clientId) {
        List<Application> applications = applicationRepository.findAllByClientId(clientId);

        // Маппим и вручную устанавливаем детали для кастомных заявок
        return applications.stream()
                .map(this::enhanceApplicationWithDetails)
                .collect(Collectors.toList());
    }

    @Transactional(readOnly = true)
    public ApplicationDto getApplicationById(Integer id) {
        Application application = applicationRepository.findByIdWithDetails(id)
                .orElseThrow(() -> new ResourceNotFoundException("Заявка", id));

        return enhanceApplicationWithDetails(application);
    }

    /**
     * Дополняет ApplicationDto деталями заявки
     */
    private ApplicationDto enhanceApplicationWithDetails(Application application) {
        ApplicationDto dto = applicationMapper.toDto(application);

        // Если это кастомная заявка - загружаем детали вручную
        if (application.getCustomApplication() != null) {
            List<ApplicationDetail> details = applicationDetailRepository
                    .findAll().stream()
                    .filter(detail -> detail.getId().getApplicationId().equals(application.getId()))
                    .collect(Collectors.toList());
            dto.setDetails(applicationDetailMapper.toDto(details));
        }

        return dto;
    }

}
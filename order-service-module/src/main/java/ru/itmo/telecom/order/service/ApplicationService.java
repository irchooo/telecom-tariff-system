package ru.itmo.telecom.order.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
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
import ru.itmo.telecom.shared.order.dto.ApplicationDetailDto;
import ru.itmo.telecom.shared.order.dto.ApplicationDetailRequest;
import ru.itmo.telecom.shared.order.dto.ApplicationDto;
import ru.itmo.telecom.shared.tariff.dto.ServiceParameterDto;
import ru.itmo.telecom.shared.tariff.dto.TariffDetailDto;
import ru.itmo.telecom.shared.tariff.dto.TariffDto;
import ru.itmo.telecom.shared.utils.TelecomConstants;

import java.math.BigDecimal;
import java.util.*;
import java.util.stream.Collectors;

@Slf4j
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
        log.debug("=== Starting application creation ===");

        try {
            // Получаем статус "СОЗДАНА"
            ApplicationStatus createdStatus = statusRepository.findByName(TelecomConstants.APP_STATUS_CREATED)
                    .orElseThrow(() -> new ResourceNotFoundException("Статус заявки", TelecomConstants.APP_STATUS_CREATED));

            // Создаем основную заявку
            Application application = new Application();
            application.setClientId(createRequest.getClientId());
            application.setStatus(createdStatus);

            // Рассчитываем стоимость
            BigDecimal totalCost = calculateTotalCost(createRequest);
            application.setTotalCost(totalCost);

            // Сохраняем основную заявку
            Application savedApplication = applicationRepository.save(application);
            log.debug("Saved application with ID: {}", savedApplication.getId());

            // Создаем запись в истории статусов
            ApplicationStatusHistory statusHistory = new ApplicationStatusHistory();
            statusHistory.setApplication(savedApplication);
            statusHistory.setStatus(createdStatus);
            statusHistory.setComment("Заявка создана через Telegram бота");
            statusHistoryRepository.save(statusHistory);

            // Обрабатываем тип заявки
            if (createRequest.getTariffId() != null) {
                // Заявка на готовый тариф
                TariffApplication tariffApplication = new TariffApplication();
                tariffApplication.setApplication(savedApplication);
                tariffApplication.setTariffId(createRequest.getTariffId());
                tariffApplicationRepository.save(tariffApplication);
                log.debug("Created tariff application");

            } else if (createRequest.getDetails() != null && !createRequest.getDetails().isEmpty()) {
                // Кастомная заявка (конструктор)
                CustomApplication customApplication = new CustomApplication();
                customApplication.setApplication(savedApplication);
                customApplicationRepository.save(customApplication);
                log.debug("Created custom application for application ID: {}", savedApplication.getId());

                // Сохраняем детали заявки
                saveApplicationDetails(savedApplication, createRequest.getDetails());
                log.debug("Saved application details");
            }

            // Перезагружаем заявку с деталями для возврата
            Application loadedApplication = applicationRepository.findByIdWithDetails(savedApplication.getId())
                    .orElse(savedApplication);

            ApplicationDto result = applicationMapper.toDto(loadedApplication);

            // Вручную устанавливаем детали если это кастомная заявка
            if (createRequest.getDetails() != null && !createRequest.getDetails().isEmpty()) {
                List<ApplicationDetail> details = applicationDetailRepository.findAll().stream()
                        .filter(detail -> detail.getId().getApplicationId().equals(savedApplication.getId()))
                        .collect(Collectors.toList());
                result.setDetails(applicationDetailMapper.toDto(details));
            }

            log.debug("=== Application creation completed successfully ===");
            return result;

        } catch (Exception e) {
            log.error("Error creating application", e);
            throw new RuntimeException("Ошибка при создании заявки: " + e.getMessage(), e);
        }
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
            // Получаем полную информацию о тарифе с деталями
            String tariffUrl = "http://localhost:8081/api/v1/tariffs/{id}";
            ResponseEntity<TariffDto> response = restTemplate.getForEntity(
                    tariffUrl, TariffDto.class, tariffId);

            if (response.getStatusCode().is2xxSuccessful() && response.getBody() != null) {
                TariffDto tariff = response.getBody();
                BigDecimal totalCost = BigDecimal.ZERO;

                // Рассчитываем по формуле: SUM(volume * (max_price_per_unit * price_coefficient))
                for (TariffDetailDto detail : tariff.getDetails()) {
                    BigDecimal parameterCost = detail.getParameter().getMaxPricePerUnit()
                            .multiply(detail.getPriceCoefficient())
                            .multiply(BigDecimal.valueOf(detail.getVolume()));
                    totalCost = totalCost.add(parameterCost);

                    log.debug("Tariff detail: {} {} * ({} * {}) = {}",
                            detail.getVolume(), detail.getParameter().getUnit(),
                            detail.getParameter().getMaxPricePerUnit(), detail.getPriceCoefficient(),
                            parameterCost);
                }

                log.debug("Calculated predefined tariff cost: {}", totalCost);
                return totalCost;
            }
        } catch (Exception e) {
            log.error("Ошибка при расчете стоимости готового тарифа: {}", e.getMessage());
        }
        return BigDecimal.ZERO;
    }

    private BigDecimal calculateCustomTariffCost(List<ApplicationDetailRequest> details) {
        BigDecimal totalCost = BigDecimal.ZERO;

        try {
            // Получаем актуальные цены из tariff-service
            String parametersUrl = "http://localhost:8081/api/v1/tariffs/parameters";
            ResponseEntity<ServiceParameterDto[]> response =
                    restTemplate.getForEntity(parametersUrl, ServiceParameterDto[].class);

            if (response.getStatusCode().is2xxSuccessful() && response.getBody() != null) {
                // Создаем мапу цен для быстрого доступа
                Map<Integer, BigDecimal> currentPrices = Arrays.stream(response.getBody())
                        .collect(Collectors.toMap(
                                ServiceParameterDto::getId,
                                ServiceParameterDto::getMaxPricePerUnit
                        ));

                for (ApplicationDetailRequest detail : details) {
                    BigDecimal unitPrice = currentPrices.getOrDefault(detail.getParameterId(), BigDecimal.ZERO);
                    BigDecimal parameterCost = unitPrice.multiply(BigDecimal.valueOf(detail.getVolume()));
                    totalCost = totalCost.add(parameterCost);

                    log.debug("Custom parameter {}: {} units * {} = {}",
                            detail.getParameterId(), detail.getVolume(), unitPrice, parameterCost);
                }
            }
        } catch (Exception e) {
            log.error("Ошибка при получении цен параметров: {}", e.getMessage());
            // Fallback на временные цены
            return calculateCustomTariffCostFallback(details);
        }

        log.debug("Total custom tariff cost: {}", totalCost);
        return totalCost;
    }

    private BigDecimal calculateCustomTariffCostFallback(List<ApplicationDetailRequest> details) {
        BigDecimal totalCost = BigDecimal.ZERO;
        Map<Integer, BigDecimal> fallbackPrices = Map.of(
                1, new BigDecimal("50.00"), // Интернет
                2, new BigDecimal("1.50"),  // Минуты
                3, new BigDecimal("1.00")   // SMS
        );

        for (ApplicationDetailRequest detail : details) {
            BigDecimal unitPrice = fallbackPrices.getOrDefault(detail.getParameterId(), BigDecimal.ZERO);
            BigDecimal parameterCost = unitPrice.multiply(BigDecimal.valueOf(detail.getVolume()));
            totalCost = totalCost.add(parameterCost);
        }

        log.warn("Using fallback prices for custom tariff calculation");
        return totalCost;
    }

    private void saveApplicationDetails(Application application, List<ApplicationDetailRequest> details) {
        List<ApplicationDetail> applicationDetails = new ArrayList<>();

        for (ApplicationDetailRequest detailRequest : details) {
            try {
                ApplicationDetail detail = new ApplicationDetail();
                ApplicationDetailId detailId = new ApplicationDetailId();
                detailId.setApplicationId(application.getId());
                detailId.setParameterId(detailRequest.getParameterId());

                detail.setId(detailId);
                detail.setApplication(application);
                detail.setVolume(detailRequest.getVolume());
                detail.setUnitPrice(getCurrentUnitPrice(detailRequest.getParameterId()));

                applicationDetails.add(detail);
                log.debug("Prepared application detail for parameter: {}", detailRequest.getParameterId());

            } catch (Exception e) {
                log.warn("Failed to prepare application detail for parameter {}: {}",
                        detailRequest.getParameterId(), e.getMessage());
            }
        }

        // Сохраняем все детали одним запросом
        if (!applicationDetails.isEmpty()) {
            applicationDetailRepository.saveAll(applicationDetails);
            log.debug("Saved {} application details", applicationDetails.size());
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
            log.warn("Failed to get current price for parameter {}: {}", parameterId, e.getMessage());
        }

        // Fallback цены
        Map<Integer, BigDecimal> fallbackPrices = Map.of(
                1, new BigDecimal("50.00"), // Интернет
                2, new BigDecimal("1.50"),  // Минуты
                3, new BigDecimal("1.00")   // SMS
        );
        return fallbackPrices.getOrDefault(parameterId, BigDecimal.ZERO);
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
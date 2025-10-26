package ru.itmo.telecom.tariff.service;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.itmo.telecom.shared.tariff.dto.TariffCreateRequest;
import ru.itmo.telecom.shared.tariff.dto.TariffDetailCreateRequest;
import ru.itmo.telecom.shared.tariff.dto.TariffDto;
import ru.itmo.telecom.tariff.entity.ServiceParameter;
import ru.itmo.telecom.tariff.entity.Tariff;
import ru.itmo.telecom.tariff.entity.TariffDetail;
import ru.itmo.telecom.tariff.entity.TariffDetailId;
import ru.itmo.telecom.tariff.mapper.TariffDetailMapper;
import ru.itmo.telecom.tariff.mapper.TariffMapper;
import ru.itmo.telecom.tariff.repository.ServiceParameterRepository;
import ru.itmo.telecom.tariff.repository.TariffRepository;

import java.util.List;

@Service
@RequiredArgsConstructor
public class TariffService {

    private final TariffRepository tariffRepository;
    private final ServiceParameterRepository parameterRepository;
    private final TariffMapper tariffMapper;
    private final TariffDetailMapper detailMapper;

    @Transactional(readOnly = true)
    public List<TariffDto> findAllTariffs() {
        return tariffMapper.toDto(tariffRepository.findAll());
    }

    @Transactional
    public TariffDto createTariff(TariffCreateRequest createRequest) {

        // 1. Проверка на дубликаты тарифа
        tariffRepository.findByName(createRequest.getName()).ifPresent(t -> {
            throw new RuntimeException("Тариф с именем '" + createRequest.getName() + "' уже существует.");
        });

        // 2. Создание основной сущности Tariff
        Tariff tariff = tariffMapper.toEntity(createRequest);
        Tariff savedTariff = tariffRepository.save(tariff);

        // 3. Создание TariffDetails
        List<TariffDetail> details = createRequest.getDetails().stream()
                .map(detailRequest -> mapAndValidateDetail(detailRequest, savedTariff))
                .toList();

        savedTariff.setDetails(details);

        // 4. Сохранение (благодаря CascadeType.ALL в Tariff, детали сохранятся автоматически)
        return tariffMapper.toDto(savedTariff);
    }

    // Вспомогательный метод для маппинга и проверки деталей
    private TariffDetail mapAndValidateDetail(TariffDetailCreateRequest detailRequest, Tariff tariff) {

        // Поиск ServiceParameter для внешнего ключа
        ServiceParameter parameter = parameterRepository.findById(detailRequest.getParameterId())
                .orElseThrow(() -> new RuntimeException("Параметр услуги с ID " + detailRequest.getParameterId() + " не найден."));

        // Маппинг Request DTO в Entity
        TariffDetail detail = detailMapper.toEntity(detailRequest);

        // Установка внешних ключей и составного ключа ID
        detail.setTariff(tariff);
        detail.setParameter(parameter);

        detail.setId(new TariffDetailId(tariff.getId(), parameter.getId()));

        return detail;
    }
}

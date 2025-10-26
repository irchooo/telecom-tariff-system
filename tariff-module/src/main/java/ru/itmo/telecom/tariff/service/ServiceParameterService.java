package ru.itmo.telecom.tariff.service;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.itmo.telecom.tariff.entity.ServiceParameter;
import ru.itmo.telecom.tariff.mapper.ServiceParameterMapper;
import ru.itmo.telecom.tariff.repository.ServiceParameterRepository;
import ru.itmo.telecom.shared.tariff.dto.ServiceParameterDto;
import ru.itmo.telecom.shared.tariff.dto.ServiceParameterCreateRequest;

import java.util.List;

@Service
@RequiredArgsConstructor
public class ServiceParameterService {

    private final ServiceParameterRepository repository;
    private final ServiceParameterMapper mapper;

    @Transactional(readOnly = true)
    public List<ServiceParameterDto> findAllParameters() {
        List<ServiceParameter> parameters = repository.findAll();
        return mapper.toDto(parameters);
    }

    @Transactional
    public ServiceParameterDto createParameter(ServiceParameterCreateRequest createRequest) {

        // 1. Проверить на дубликаты
        repository.findByName(createRequest.getName()).ifPresent(p -> {
            // В реальном приложении здесь должно быть:
            // throw new DuplicateEntityException("Параметр услуги с именем " + createRequest.getName() + " уже существует.");

            // Пока используем стандартное исключение (до создания кастомных)
            throw new RuntimeException("Параметр услуги с именем '" + createRequest.getName() + "' уже существует.");
        });

        // 2. Создать Entity из DTO-запроса
        ServiceParameter parameter = mapper.toEntity(createRequest);

        // 3. Сохранить Entity (Hibernate заполнит ID и created_at)
        ServiceParameter savedParameter = repository.save(parameter);

        // 4. Вернуть DTO
        return mapper.toDto(savedParameter);
    }
}

package ru.itmo.telecom.tariff.service;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.itmo.telecom.shared.exceptions.ResourceNotFoundException;
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

    // ДОБАВЛЯЕМ ЭТОТ МЕТОД - поиск параметра по ID
    @Transactional(readOnly = true)
    public ServiceParameterDto findParameterById(Integer id) {
        ServiceParameter parameter = repository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Параметр услуги", id));
        return mapper.toDto(parameter);
    }

    @Transactional
    public ServiceParameterDto createParameter(ServiceParameterCreateRequest createRequest) {
        // Проверка на дубликаты
        repository.findByName(createRequest.getName()).ifPresent(p -> {
            throw new RuntimeException("Параметр услуги с именем '" + createRequest.getName() + "' уже существует.");
        });

        ServiceParameter parameter = mapper.toEntity(createRequest);
        ServiceParameter savedParameter = repository.save(parameter);
        return mapper.toDto(savedParameter);
    }
}
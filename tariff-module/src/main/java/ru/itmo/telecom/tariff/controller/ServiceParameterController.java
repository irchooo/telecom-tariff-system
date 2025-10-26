package ru.itmo.telecom.tariff.controller;

import jakarta.validation.Valid; // Важно для валидации
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;
import ru.itmo.telecom.tariff.service.ServiceParameterService;
import ru.itmo.telecom.shared.tariff.dto.ServiceParameterDto;
import ru.itmo.telecom.shared.tariff.dto.ServiceParameterCreateRequest;

import java.util.List;

@RestController
@RequestMapping("/parameters") // Относительный путь: /api/v1/tariffs/parameters
@RequiredArgsConstructor // Используем Lombok для конструктора
public class ServiceParameterController {

    // 1. Используем Service, а не Repository
    private final ServiceParameterService service;

    /**
     * Получает все параметры услуг (возвращает DTO)
     */
    @GetMapping
    public List<ServiceParameterDto> getAllParameters() {
        // 2. Вызываем метод сервиса
        return service.findAllParameters();
    }

    /**
     * Создает новый параметр услуги
     */
    @PostMapping // 3. Обрабатываем POST-запросы
    @ResponseStatus(HttpStatus.CREATED)
    public ServiceParameterDto createParameter(
            @Valid @RequestBody ServiceParameterCreateRequest createRequest) {

        // 4. Вызываем метод сервиса для создания
        return service.createParameter(createRequest);
    }
}

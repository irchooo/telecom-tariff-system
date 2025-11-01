package ru.itmo.telecom.tariff.controller;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;
import ru.itmo.telecom.tariff.service.ServiceParameterService;
import ru.itmo.telecom.shared.tariff.dto.ServiceParameterDto;
import ru.itmo.telecom.shared.tariff.dto.ServiceParameterCreateRequest;

import java.util.List;

@RestController
@RequestMapping("/parameters")
@RequiredArgsConstructor
public class ServiceParameterController {

    private final ServiceParameterService service;

    @GetMapping
    public List<ServiceParameterDto> getAllParameters() {
        return service.findAllParameters();
    }

    // ДОБАВЛЯЕМ ЭТОТ МЕТОД - получение параметра по ID
    @GetMapping("/{id}")
    public ServiceParameterDto getParameterById(@PathVariable Integer id) {
        return service.findParameterById(id);
    }

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public ServiceParameterDto createParameter(@Valid @RequestBody ServiceParameterCreateRequest createRequest) {
        return service.createParameter(createRequest);
    }
}
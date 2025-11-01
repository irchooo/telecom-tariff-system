package ru.itmo.telecom.order.controller;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;
import ru.itmo.telecom.order.service.ApplicationService;
import ru.itmo.telecom.shared.order.dto.ApplicationCreateRequest;
import ru.itmo.telecom.shared.order.dto.ApplicationDto;

import java.util.List;

@RestController
@RequestMapping("/applications")
@RequiredArgsConstructor
public class ApplicationController {

    private final ApplicationService service;

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public ApplicationDto createApplication(@Valid @RequestBody ApplicationCreateRequest createRequest) {
        return service.createApplication(createRequest);
    }

    @GetMapping("/client/{clientId}")
    public List<ApplicationDto> getApplicationsByClient(@PathVariable Integer clientId) {
        return service.getApplicationsByClientId(clientId);
    }

    @GetMapping("/{id}")
    public ApplicationDto getApplication(@PathVariable Integer id) {
        return service.getApplicationById(id);
    }
}

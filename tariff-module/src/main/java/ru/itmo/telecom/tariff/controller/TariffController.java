package ru.itmo.telecom.tariff.controller;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RequestBody;
import ru.itmo.telecom.shared.tariff.dto.TariffCreateRequest;
import ru.itmo.telecom.shared.tariff.dto.TariffDto;
import ru.itmo.telecom.tariff.service.TariffService;

import java.util.List;

@RestController
@RequestMapping
@RequiredArgsConstructor
public class TariffController {

    private final TariffService service;

    @GetMapping
    public List<TariffDto> getAllTariffs() {
        return service.findAllTariffs();
    }

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public TariffDto createTariff(@Valid @RequestBody TariffCreateRequest createRequest) {
        return service.createTariff(createRequest);
    }
}

package ru.itmo.telecom.tariff.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import ru.itmo.telecom.tariff.entity.ServiceParameter;
import ru.itmo.telecom.tariff.repository.ServiceParameterRepository;

import java.util.List;

@RestController
@RequestMapping("/parameters") // Относительный путь: /api/v1/tariffs/parameters
public class ServiceParameterController {

    private final ServiceParameterRepository repository;

    @Autowired
    public ServiceParameterController(ServiceParameterRepository repository) {
        this.repository = repository;
    }

    @GetMapping
    public List<ServiceParameter> getAllParameters() {
        return repository.findAll();
    }
}

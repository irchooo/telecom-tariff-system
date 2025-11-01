package ru.itmo.telecom.tariff.config;

import lombok.RequiredArgsConstructor;
import org.springframework.boot.CommandLineRunner;
import org.springframework.stereotype.Component;
import ru.itmo.telecom.tariff.entity.ServiceParameter;
import ru.itmo.telecom.tariff.repository.ServiceParameterRepository;

import java.math.BigDecimal;
import java.util.Arrays;
import java.util.List;

@Component
@RequiredArgsConstructor
public class DataInitializer implements CommandLineRunner {

    private final ServiceParameterRepository serviceParameterRepository;

    @Override
    public void run(String... args) throws Exception {
        System.out.println("=== Starting Tariff Service Data Initialization ===");

        // Инициализация параметров услуг
        if (serviceParameterRepository.count() == 0) {
            System.out.println("Initializing service parameters...");

            List<ServiceParameter> parameters = Arrays.asList(
                    createParameter("Интернет", "Гб", new BigDecimal("2.00"), "Доступ в интернет"),
                    createParameter("Минуты", "мин", new BigDecimal("1.00"), "Звонки на все номера"),
                    createParameter("SMS", "шт", new BigDecimal("0.50"), "SMS сообщения")
            );

            serviceParameterRepository.saveAll(parameters);
            System.out.println("Service parameters initialized: " + parameters.size());

            // Выведем созданные ID для отладки
            parameters.forEach(p ->
                    System.out.println("Created parameter: ID=" + p.getId() + ", Name=" + p.getName())
            );
        } else {
            System.out.println("Service parameters already exist: " + serviceParameterRepository.count());
            // Выведем существующие параметры
            serviceParameterRepository.findAll().forEach(p ->
                    System.out.println("Existing parameter: ID=" + p.getId() + ", Name=" + p.getName())
            );
        }

        System.out.println("=== Tariff Service Data Initialization Complete ===");
    }

    private ServiceParameter createParameter(String name, String unit, BigDecimal price, String description) {
        ServiceParameter parameter = new ServiceParameter();
        parameter.setName(name);
        parameter.setUnit(unit);
        parameter.setMaxPricePerUnit(price);
        parameter.setDescription(description);
        parameter.setIsActive(true);
        return parameter;
    }
}

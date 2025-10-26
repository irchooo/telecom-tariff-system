package ru.itmo.telecom.tariff;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.ComponentScan;

@SpringBootApplication
@ComponentScan(basePackages = {"ru.itmo.telecom.tariff", "ru.itmo.telecom.shared"})
public class TariffApplication {

    public static void main(String[] args) {
        SpringApplication.run(TariffApplication.class, args);
    }
}

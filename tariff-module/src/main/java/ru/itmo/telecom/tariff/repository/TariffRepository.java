package ru.itmo.telecom.tariff.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import ru.itmo.telecom.tariff.entity.Tariff;

import java.util.Optional;

public interface TariffRepository extends JpaRepository<Tariff, Integer> {
    // Spring Data JPA автоматически предоставит CRUD-методы

    Optional<Tariff> findByName(String name);
}

package ru.itmo.telecom.tariff.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import ru.itmo.telecom.tariff.entity.ServiceParameter;

public interface ServiceParameterRepository extends JpaRepository<ServiceParameter, Integer> {
    // Spring Data JPA автоматически предоставит CRUD-методы
}

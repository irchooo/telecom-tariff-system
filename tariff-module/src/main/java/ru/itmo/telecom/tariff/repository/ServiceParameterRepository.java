package ru.itmo.telecom.tariff.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import ru.itmo.telecom.tariff.entity.ServiceParameter;

import java.util.Optional;

public interface ServiceParameterRepository extends JpaRepository<ServiceParameter, Integer> {
    // Spring Data JPA автоматически предоставит CRUD-методы

    //метод для проверки уникальности имени
    Optional<ServiceParameter> findByName(String name);
}

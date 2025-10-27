package ru.itmo.telecom.order.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import ru.itmo.telecom.order.entity.ApplicationStatus;

import java.util.Optional;

public interface ApplicationStatusRepository extends JpaRepository<ApplicationStatus, Integer> {

    // Полезный метод для поиска статуса по имени
    Optional<ApplicationStatus> findByName(String name);
}

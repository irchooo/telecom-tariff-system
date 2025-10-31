package ru.itmo.telecom.order.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import ru.itmo.telecom.order.entity.CustomApplication;

public interface CustomApplicationRepository extends JpaRepository<CustomApplication, Integer> {
}
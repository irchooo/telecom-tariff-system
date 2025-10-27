package ru.itmo.telecom.order.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import ru.itmo.telecom.order.entity.Application;

public interface ApplicationRepository extends JpaRepository<Application, Integer> {
}

package ru.itmo.telecom.order.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import ru.itmo.telecom.order.entity.ApplicationStatusHistory;

public interface ApplicationStatusHistoryRepository extends JpaRepository<ApplicationStatusHistory, Long> {
}

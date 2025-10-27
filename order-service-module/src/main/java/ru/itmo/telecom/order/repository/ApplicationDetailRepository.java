package ru.itmo.telecom.order.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import ru.itmo.telecom.order.entity.ApplicationDetail;
import ru.itmo.telecom.order.entity.ApplicationDetailId;

public interface ApplicationDetailRepository extends JpaRepository<ApplicationDetail, ApplicationDetailId> {
}

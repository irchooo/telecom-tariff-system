package ru.itmo.telecom.order.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import ru.itmo.telecom.order.entity.TariffApplication;

public interface TariffApplicationRepository extends JpaRepository<TariffApplication, Integer> {
}

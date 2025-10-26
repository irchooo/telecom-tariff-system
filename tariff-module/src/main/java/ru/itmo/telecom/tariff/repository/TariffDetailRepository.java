package ru.itmo.telecom.tariff.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import ru.itmo.telecom.tariff.entity.TariffDetail;
import ru.itmo.telecom.tariff.entity.TariffDetailId;

public interface TariffDetailRepository extends JpaRepository<TariffDetail, TariffDetailId> {

    // Spring Data JPA будет использовать TariffDetailId как тип для PK.
}

package ru.itmo.telecom.shared.tariff.dto;

import lombok.Value;
import java.time.Instant;
import java.util.List;

@Value
public class TariffDto {

    Integer id;
    String name;         // Название тарифа
    String description;
    Boolean isActive;
    Instant createdAt;

    // Вложенный список деталей тарифа (состав тарифа)
    List<TariffDetailDto> details;
}

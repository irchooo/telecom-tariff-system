package ru.itmo.telecom.shared.tariff.dto;

import lombok.Value;
import java.math.BigDecimal;
import java.time.Instant;

@Value // Используем @Value для иммутабельности
public class ServiceParameterDto {

    Integer id;
    String name;         // Название параметра (Минуты, SMS, Гигабайты)
    String unit;         // Единица измерения (мин., шт., Гб)
    BigDecimal maxPricePerUnit; // Максимальная цена за единицу
    String description;
    Boolean isActive;
    Instant createdAt;
}

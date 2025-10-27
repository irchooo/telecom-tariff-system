package ru.itmo.telecom.shared.tariff.dto;

import lombok.Value;
import java.math.BigDecimal;
import java.time.Instant;

@Value
public class TariffDetailDto {

    Integer tariffId;
    Integer parameterId;

    // Вложенный DTO для отображения информации об услуге
    ServiceParameterDto parameter;

    Integer volume;               // Количество единиц услуги
    BigDecimal priceCoefficient;  // Коэффициент цены
    String description;
    Boolean isActive;
    Instant createdAt;
}

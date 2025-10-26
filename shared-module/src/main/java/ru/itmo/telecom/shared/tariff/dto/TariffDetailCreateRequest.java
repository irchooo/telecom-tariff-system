package ru.itmo.telecom.shared.tariff.dto;

import jakarta.validation.constraints.DecimalMax;
import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;
import lombok.Value;
import java.math.BigDecimal;

@Value
public class TariffDetailCreateRequest {

    @NotNull(message = "ID параметра услуги обязателен")
    Integer parameterId;

    @Min(value = 1, message = "Объем должен быть не менее 1")
    @NotNull(message = "Объем услуги обязателен")
    Integer volume;

    @NotNull(message = "Коэффициент цены обязателен")
    @DecimalMin(value = "0.01", message = "Коэффициент должен быть положительным")
    @DecimalMax(value = "9.99", message = "Коэффициент не может превышать 9.99")
    BigDecimal priceCoefficient;

    String description;
}

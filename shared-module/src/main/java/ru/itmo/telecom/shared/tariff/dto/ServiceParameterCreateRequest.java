package ru.itmo.telecom.shared.tariff.dto;

import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.Value;

import java.math.BigDecimal;

@Value
public class ServiceParameterCreateRequest {

    @NotBlank(message = "Имя параметра не может быть пустым")
    @Size(max = 50, message = "Имя должно быть короче 50 символов")
    String name;

    @NotBlank(message = "Единица измерения не может быть пустой")
    @Size(max = 10, message = "Единица измерения должна быть короче 10 символов")
    String unit;

    @NotNull(message = "Цена за единицу обязательна")
    @DecimalMin(value = "0.01", message = "Цена должна быть положительной")
    BigDecimal maxPricePerUnit;

    @Size(max = 200, message = "Описание должно быть короче 200 символов")
    String description;
}

package ru.itmo.telecom.shared.order.dto;

import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * Детали для кастомной заявки (конструктора).
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
public class ApplicationDetailRequest {

    // ID параметра услуги (из tariff-module)
    @NotNull(message = "ID параметра услуги обязателен")
    private Integer parameterId;

    // Требуемый объем (ГБ, минуты, SMS)
    @Min(value = 0, message = "Объем должен быть не менее 0")
    @NotNull(message = "Объем услуги обязателен")
    private Integer volume;
}

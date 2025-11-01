package ru.itmo.telecom.shared.order.dto;

import lombok.Value;

import java.math.BigDecimal;

/**
 * DTO для детали заявки (фактический состав заказа).
 */
@Value
public class ApplicationDetailDto {
    // ID параметра услуги (для отображения имени)
    Integer parameterId;

    // Объем (ГБ, минуты и т.д.)
    Integer volume;

    // Фактическая цена за единицу на момент заявки
    BigDecimal unitPrice;
}

package ru.itmo.telecom.shared.order.dto;

import jakarta.validation.Valid;
import jakarta.validation.constraints.NotNull;
import lombok.Data;
import java.util.List;

/**
 * Запрос на создание новой заявки.
 * Должен содержать ЛИБО tariffId, ЛИБО details.
 */
@Data
public class ApplicationCreateRequest {

    // ID клиента (на данный момент передаем явно, позже заменим на извлечение из JWT/сессии)
    @NotNull(message = "ID клиента обязателен")
    private Integer clientId;

    // ID готового тарифа (если заявка на готовый тариф)
    // Должен быть NULL, если используется конструктор.
    private Integer tariffId;

    // Детали для конструктора (если tariffId NULL)
    @Valid // Валидация для каждого элемента в списке
    private List<ApplicationDetailRequest> details;
}

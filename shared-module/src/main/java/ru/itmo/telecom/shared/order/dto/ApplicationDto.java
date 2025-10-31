package ru.itmo.telecom.shared.order.dto;

import lombok.Data;
import java.math.BigDecimal;
import java.time.Instant;
import java.util.List;

/**
 * Основной DTO для заявки.
 */
@Data
public class ApplicationDto {
    private Integer id;
    private Integer clientId;

    // Ссылка на готовый тариф, если есть
    private Integer tariffId;

    private BigDecimal totalCost;
    private ApplicationStatusDto status; // Вложенный DTO

    // Детали, которые будут присутствовать только у CustomApplication
    private List<ApplicationDetailDto> details;

    private Boolean isActive;
    private Instant createdAt;
}

package ru.itmo.telecom.shared.order.dto;

import lombok.Value;

/**
 * DTO для справочника статусов заявки.
 */
@Value
public class ApplicationStatusDto {
    Integer id;
    String name;
    String description;
}

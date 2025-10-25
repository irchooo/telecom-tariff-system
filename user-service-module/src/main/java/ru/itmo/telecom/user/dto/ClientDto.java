package ru.itmo.telecom.user.dto;

import lombok.Data;
import java.time.Instant;

/**
 * DTO для отображения информации о клиенте.
 */
@Data
public class ClientDto {
    private Integer id;
    private Long telegramId;
    private String telegramUsername;
    private String firstName;
    private String lastName;
    private String phoneNumber;
    private Instant registrationDate;
    private String status; // Отображаем имя статуса, а не ID
}

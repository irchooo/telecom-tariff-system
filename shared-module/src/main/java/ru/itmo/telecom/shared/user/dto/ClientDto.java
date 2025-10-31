package ru.itmo.telecom.shared.user.dto;

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
    private String email;
    private Instant registrationDate;
    private String status; // Имя статуса (например, "АКТИВНЫЙ", "НОВЫЙ")
}

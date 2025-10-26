package ru.itmo.telecom.user.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import lombok.Data;
import ru.itmo.telecom.shared.utils.TelecomConstants;

/**
 * DTO для обновления данных профиля клиента.
 */
@Data
public class ClientUpdateDto {

    @NotBlank(message = "Имя не может быть пустым")
    private String firstName;

    @NotBlank(message = "Фамилия не может быть пустой")
    private String lastName;

    @NotBlank(message = "Номер телефона не может быть пустым")
    @Pattern(regexp = TelecomConstants.PHONE_REGEX, message = "Неверный формат номера телефона")
    private String phoneNumber;
}

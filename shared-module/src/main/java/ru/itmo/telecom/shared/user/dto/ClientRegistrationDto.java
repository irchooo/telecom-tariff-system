package ru.itmo.telecom.shared.user.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Pattern;
import lombok.Data;
import ru.itmo.telecom.shared.utils.TelecomConstants;

/**
 * DTO для процесса регистрации нового клиента.
 */
@Data
public class ClientRegistrationDto {

    @NotNull(message = "Telegram ID не может быть null")
    private Long telegramId;

    private String telegramUsername;

    @NotBlank(message = "Имя не может быть пустым")
    private String firstName;

    @NotBlank(message = "Фамилия не может быть пустой")
    private String lastName;

    @NotBlank(message = "Номер телефона не может быть пустым")
    @Pattern(regexp = TelecomConstants.PHONE_REGEX, message = "Неверный формат номера телефона")
    private String phoneNumber;
}

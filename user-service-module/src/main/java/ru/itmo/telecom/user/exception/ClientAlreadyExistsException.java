package ru.itmo.telecom.user.exception;

import ru.itmo.telecom.shared.exceptions.BaseTelecomException;

/**
 * Исключение, выбрасываемое при попытке регистрации клиента,
 * который уже существует (по Telegram ID или номеру телефона).
 */
public class ClientAlreadyExistsException extends BaseTelecomException {
    public ClientAlreadyExistsException(String message) {
        super(message);
    }
}

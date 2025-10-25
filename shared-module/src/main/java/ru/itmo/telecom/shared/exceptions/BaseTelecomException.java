package ru.itmo.telecom.shared.exceptions;

/**
 * Базовый класс для всех кастомных исключений в приложении.
 */
public class BaseTelecomException extends RuntimeException {

    public BaseTelecomException(String message) {
        super(message);
    }

    public BaseTelecomException(String message, Throwable cause) {
        super(message, cause);
    }
}

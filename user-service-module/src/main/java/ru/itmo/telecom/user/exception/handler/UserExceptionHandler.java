package ru.itmo.telecom.user.exception.handler;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import ru.itmo.telecom.shared.dto.ErrorDto;
import ru.itmo.telecom.user.exception.ClientAlreadyExistsException;

/**
 * Локальный обработчик исключений для user-service-module.
 * Дополняет GlobalExceptionHandler из shared-module.
 */
@RestControllerAdvice
public class UserExceptionHandler {

    /**
     * Обработка исключения "Клиент уже существует".
     * Возвращает статус 409 CONFLICT.
     */
    @ResponseStatus(HttpStatus.CONFLICT)
    @ExceptionHandler(ClientAlreadyExistsException.class)
    public ErrorDto handleClientAlreadyExists(ClientAlreadyExistsException ex) {
        return new ErrorDto(ex.getMessage());
    }
}

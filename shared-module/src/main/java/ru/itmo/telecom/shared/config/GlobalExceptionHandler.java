package ru.itmo.telecom.shared.config;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import ru.itmo.telecom.shared.dto.ErrorDto;
import ru.itmo.telecom.shared.exceptions.ResourceNotFoundException;

import java.util.stream.Collectors;

/**
 * Глобальный обработчик исключений,
 * аналогичный ApiExceptionHandler из образца.
 * Будет перехватывать исключения из всех контроллеров,
 * которые импортируют этот shared-module.
 */
@RestControllerAdvice
public class GlobalExceptionHandler {

    /**
     * Обработка ошибок валидации DTO.
     */
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ErrorDto handleValidationExceptions(MethodArgumentNotValidException ex) {

        String errorMessage = ex.getBindingResult().getFieldErrors().stream()
                .map(error -> error.getField() + ": " + error.getDefaultMessage())
                .collect(Collectors.joining("; "));

        return new ErrorDto(errorMessage.isEmpty() ? ex.getMessage() : errorMessage);
    }

    /**
     * Обработка исключения "Ресурс не найден".
     */
    @ResponseStatus(HttpStatus.NOT_FOUND)
    @ExceptionHandler(ResourceNotFoundException.class)
    public ErrorDto handleResourceNotFound(ResourceNotFoundException ex) {
        return new ErrorDto(ex.getMessage());
    }

    /**
     * Обработка всех остальных непредвиденных исключений
     * (более стандартный подход, чем в образце [cite: 243, 244]).
     */
    @ResponseStatus(HttpStatus.INTERNAL_SERVER_ERROR)
    @ExceptionHandler(Exception.class)
    public ErrorDto handleAllOtherExceptions(Exception ex) {
        // ВАЖНО: В реальном проекте здесь обязательно должно быть логирование
        // log.error("Unhandled exception occurred: ", ex);
        return new ErrorDto("Произошла внутренняя ошибка сервера. Пожалуйста, обратитесь в поддержку.");
    }
}
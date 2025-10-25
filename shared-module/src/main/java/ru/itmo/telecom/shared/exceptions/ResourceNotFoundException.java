package ru.itmo.telecom.shared.exceptions;

/**
 * Исключение, выбрасываемое, когда запрашиваемый ресурс не найден.
 * (Например, пользователь, тариф и т.д.)
 */
public class ResourceNotFoundException extends BaseTelecomException {

    public ResourceNotFoundException(String resourceName, Object resourceId) {
        super(String.format("%s с ID %s не найден", resourceName, resourceId.toString()));
    }

    public ResourceNotFoundException(String message) {
        super(message);
    }
}

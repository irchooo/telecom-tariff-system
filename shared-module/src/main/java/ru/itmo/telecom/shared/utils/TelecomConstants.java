package ru.itmo.telecom.shared.utils;

/**
 * Общие константы приложения.
 */
public final class TelecomConstants {

    private TelecomConstants() {} // Приватный конструктор

    // Статусы клиентов из БД [cite: 353]
    public static final String CLIENT_STATUS_ACTIVE = "АКТИВНЫЙ";
    public static final String CLIENT_STATUS_NEW = "НОВЫЙ";
    public static final String CLIENT_STATUS_SUSPENDED = "ПРИОСТАНОВЛЕН";
    public static final String CLIENT_STATUS_PROBLEM = "ПРОБЛЕМНЫЙ";
    public static final String CLIENT_STATUS_ARCHIVED = "В АРХИВЕ";

    // Статусы заявок из БД [cite: 357]
    public static final String APP_STATUS_CREATED = "СОЗДАНА";
    // ... (можно добавить остальные по аналогии)

    // Regex для валидации телефона (11 цифр, РФ) [cite: 302, 348]
    public static final String PHONE_REGEX = "^(7|8)\\d{10}$";
}
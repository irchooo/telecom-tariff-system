package ru.itmo.telecom.shared.utils;

/**
 * Общие константы приложения.
 */
public final class TelecomConstants {

    private TelecomConstants() {} // Приватный конструктор

    // Статусы клиентов из БД
    public static final String CLIENT_STATUS_ACTIVE = "АКТИВНЫЙ";
    public static final String CLIENT_STATUS_NEW = "НОВЫЙ";
    public static final String CLIENT_STATUS_SUSPENDED = "ПРИОСТАНОВЛЕН";
    public static final String CLIENT_STATUS_PROBLEM = "ПРОБЛЕМНЫЙ";
    public static final String CLIENT_STATUS_ARCHIVED = "В АРХИВЕ";

    // Статусы заявок из БД
    public static final String APP_STATUS_CREATED = "СОЗДАНА";
    public static final String APP_STATUS_IN_PROGRESS = "В ОБРАБОТКЕ";
    public static final String APP_STATUS_REQUIRES_CLARIFICATION = "ТРЕБУЕТ УТОЧНЕНИЯ";
    public static final String APP_STATUS_CONFIRMED = "ПОДТВЕРЖДЕНА";
    public static final String APP_STATUS_CONNECTING = "ПОДКЛЮЧАЕТСЯ";
    public static final String APP_STATUS_COMPLETED = "ВЫПОЛНЕНА";
    public static final String APP_STATUS_REJECTED = "ОТКЛОНЕНА";

    // Regex для валидации телефона (11 цифр, РФ)
    public static final String PHONE_REGEX = "^(7|8)\\d{10}$";

    // Имена параметров услуг
    public static final String PARAMETER_INTERNET = "Интернет";
    public static final String PARAMETER_MINUTES = "Минуты";
    public static final String PARAMETER_SMS = "SMS";
}
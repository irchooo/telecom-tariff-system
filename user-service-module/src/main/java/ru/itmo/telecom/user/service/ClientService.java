package ru.itmo.telecom.user.service;

import ru.itmo.telecom.user.dto.ClientDto;
import ru.itmo.telecom.user.dto.ClientRegistrationDto;
import ru.itmo.telecom.user.dto.ClientUpdateDto;

public interface ClientService {

    /**
     * Регистрация нового клиента в системе.
     * @param dto DTO с данными для регистрации
     * @return DTO созданного клиента
     */
    ClientDto registerClient(ClientRegistrationDto dto);

    /**
     * Поиск клиента по Telegram ID.
     * @param telegramId Уникальный идентификатор Telegram
     * @return DTO клиента
     */
    ClientDto getClientByTelegramId(Long telegramId);

    /**
     * Обновление профиля клиента.
     * @param telegramId Telegram ID клиента, чей профиль обновляется
     * @param dto DTO с новыми данными
     * @return DTO обновленного клиента
     */
    ClientDto updateClientProfile(Long telegramId, ClientUpdateDto dto);
}

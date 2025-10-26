package ru.itmo.telecom.user.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import ru.itmo.telecom.user.entity.Client;

import java.util.Optional;

public interface ClientRepository extends JpaRepository<Client, Integer> {

    /**
     * Находит клиента по его Telegram ID.
     * @param telegramId Уникальный идентификатор Telegram
     * @return Optional с клиентом
     */
    Optional<Client> findByTelegramId(Long telegramId);

    /**
     * Проверяет, существует ли клиент с таким Telegram ID.
     * @param telegramId Уникальный идентификатор Telegram
     * @return true, если существует, иначе false
     */
    boolean existsByTelegramId(Long telegramId);

    /**
     * Проверяет, существует ли клиент с таким номером телефона.
     * @param phoneNumber Номер телефона
     * @return true, если существует, иначе false
     */
    boolean existsByPhoneNumber(String phoneNumber);
}

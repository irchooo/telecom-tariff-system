package ru.itmo.telecom.user.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import ru.itmo.telecom.user.entity.ClientStatus;

import java.util.Optional;

public interface ClientStatusRepository extends JpaRepository<ClientStatus, Integer> {

    /**
     * Находит статус клиента по его системному имени (e.g., "НОВЫЙ", "АКТИВНЫЙ").
     * @param name Имя статуса
     * @return Optional со статусом
     */
    Optional<ClientStatus> findByName(String name);
}

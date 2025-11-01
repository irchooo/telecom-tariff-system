package ru.itmo.telecom.order.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import ru.itmo.telecom.order.entity.Application;

import java.util.List;
import java.util.Optional;

public interface ApplicationRepository extends JpaRepository<Application, Integer> {

    /**
     * Находит заявку по ID с загрузкой связанных сущностей
     * EntityGraph должен иметь уникальное имя
     */
    @EntityGraph(attributePaths = {
            "status",
            "customApplication",
            "tariffApplication"
    }, type = EntityGraph.EntityGraphType.FETCH)
    @Query("SELECT a FROM Application a WHERE a.id = :id")
    Optional<Application> findByIdWithDetails(@Param("id") Integer id);

    /**
     * Находит все заявки клиента с загрузкой связанных сущностей
     */
    @Query("SELECT DISTINCT a FROM Application a " +
            "LEFT JOIN FETCH a.status " +
            "LEFT JOIN FETCH a.customApplication " +
            "LEFT JOIN FETCH a.tariffApplication " +
            "WHERE a.clientId = :clientId")
    List<Application> findAllByClientId(@Param("clientId") Integer clientId);

    // Стандартный метод наследуется от JpaRepository
    // Optional<Application> findById(Integer id);

    /**
     * Проверяет существование заявки по ID клиента
     */
    boolean existsByClientId(Integer clientId);

    /**
     * Находит все заявки по ID клиента (простой метод)
     */
    List<Application> findByClientId(Integer clientId);
}
package ru.itmo.telecom.tariff.entity;

import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import jakarta.persistence.Column;
import jakarta.persistence.GenerationType;
import lombok.Getter;
import lombok.Setter;
import lombok.NoArgsConstructor;
import lombok.AllArgsConstructor;
import org.hibernate.annotations.CreationTimestamp;

import java.math.BigDecimal;
import java.time.Instant;

// 3. Таблица параметров услуг
@Entity
@Table
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class ServiceParameter {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    @Column(length = 50, nullable = false, unique = true)
    private String name; // Название параметра (Минуты, SMS, Гигабайты)

    @Column(length = 10, nullable = false)
    private String unit; // Единица измерения (мин., шт., Гб)

    @Column(nullable = false, precision = 10, scale = 2)
    private BigDecimal maxPricePerUnit; // Базовая цена за единицу

    @Column(length = 200)
    private String description;

    @Column(name = "is_active", nullable = false)
    private Boolean isActive = true;

    @CreationTimestamp
    @Column(name = "created_at", nullable = false, updatable = false)
    private Instant createdAt;
}

package ru.itmo.telecom.tariff.entity;

import jakarta.persistence.EmbeddedId;
import jakarta.persistence.Entity;
import jakarta.persistence.Column;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.FetchType;
import jakarta.persistence.MapsId;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.Table;
import lombok.Getter;
import lombok.Setter;
import lombok.NoArgsConstructor;
import lombok.AllArgsConstructor;
import org.hibernate.annotations.CreationTimestamp;

import java.math.BigDecimal;
import java.time.Instant;

@Entity
@Table
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class TariffDetail {

    // Используем класс составного ключа
    @EmbeddedId
    private TariffDetailId id;

    // Связь с таблицей tariffs
    @ManyToOne(fetch = FetchType.LAZY)
    @MapsId("tariffId") // Маппит это поле на поле tariffId в составном ключе
    @JoinColumn(name = "tariff_id", nullable = false)
    private Tariff tariff;

    // Связь с таблицей service_parameters
    @ManyToOne(fetch = FetchType.LAZY)
    @MapsId("parameterId") // Маппит это поле на поле parameterId в составном ключе
    @JoinColumn(name = "parameter_id", nullable = false)
    private ServiceParameter parameter;

    @Column(nullable = false)
    private Integer volume; // Количество единиц услуги

    // Коэффициент цены: NUMERIC(4, 2)  -> BigDecimal
    @Column(name = "price_coefficient", nullable = false, precision = 4, scale = 2)
    private BigDecimal priceCoefficient = BigDecimal.ONE;

    private String description; // Доп. описание

    @Column(name = "is_active", nullable = false)
    private Boolean isActive = true;

    @CreationTimestamp
    @Column(name = "created_at", nullable = false, updatable = false)
    private Instant createdAt;
}

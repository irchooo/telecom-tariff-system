package ru.itmo.telecom.order.entity;

import jakarta.persistence.EmbeddedId;
import jakarta.persistence.Entity;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;
import jakarta.persistence.FetchType;
import jakarta.persistence.MapsId;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.Column;
import lombok.Getter;
import lombok.Setter;
import lombok.NoArgsConstructor;
import lombok.AllArgsConstructor;

import java.math.BigDecimal;

@Entity
@Table(name = "application_details")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class ApplicationDetail {

    @EmbeddedId
    private ApplicationDetailId id;

    // Внешний ключ: Ссылка на заявку
    @ManyToOne(fetch = FetchType.LAZY)
    @MapsId("applicationId")
    @JoinColumn(name = "application_id", nullable = false)
    private Application application;

    // Ссылка на параметр услуги (из tariff-module, храним ID)
    @Column(name = "parameter_id", nullable = false)
    private Integer parameterId;

    @Column(nullable = false)
    private Integer volume; // Количество единиц услуги

    // Фактическая цена за единицу на момент заявки (важно для истории)
    @Column(name = "unit_price", nullable = false, precision = 10, scale = 2)
    private BigDecimal unitPrice;
}

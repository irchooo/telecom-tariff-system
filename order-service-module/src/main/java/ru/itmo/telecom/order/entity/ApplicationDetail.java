package ru.itmo.telecom.order.entity;

import jakarta.persistence.*;
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
    @MapsId("applicationId") // Это связывает с полем applicationId в составном ключе
    @JoinColumn(name = "application_id", nullable = false)
    private Application application;

    // Ссылка на параметр услуги (из tariff-module, храним ID)
    // УДАЛИТЕ это поле - оно уже есть в составном ключе!
    // @Column(name = "parameter_id", nullable = false)
    // private Integer parameterId;

    @Column(nullable = false)
    private Integer volume; // Количество единиц услуги

    // Фактическая цена за единицу на момент заявки (важно для истории)
    @Column(name = "unit_price", nullable = false, precision = 10, scale = 2)
    private BigDecimal unitPrice;
}

package ru.itmo.telecom.order.entity;

import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.OneToOne;
import jakarta.persistence.Table;
import jakarta.persistence.FetchType;
import jakarta.persistence.MapsId;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.Column;
import lombok.Getter;
import lombok.Setter;
import lombok.NoArgsConstructor;
import lombok.AllArgsConstructor;

@Entity
@Table(name = "tariff_applications")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class TariffApplication {

    // ID - это одновременно первичный ключ и внешний ключ к applications
    @Id
    private Integer id;

    // Ссылка на родительскую заявку (One-to-One)
    @OneToOne(fetch = FetchType.LAZY)
    @MapsId // Указывает, что ID этого объекта будет использоваться как FK
    @JoinColumn(name = "application_id")
    private Application application;

    // ID тарифа из tariff-module (внешний сервис)
    @Column(name = "tariff_id", nullable = false)
    private Integer tariffId;
}

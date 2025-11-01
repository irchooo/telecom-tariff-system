package ru.itmo.telecom.order.entity;

import jakarta.persistence.*;
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
    @GeneratedValue(strategy = GenerationType.IDENTITY) // Добавьте авто-генерацию ID
    private Integer id;

    // Ссылка на родительскую заявку (One-to-One)
    @OneToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "application_id", unique = true, nullable = false)
    private Application application;

    // ID тарифа из tariff-module (внешний сервис)
    @Column(name = "tariff_id", nullable = false)
    private Integer tariffId;
}

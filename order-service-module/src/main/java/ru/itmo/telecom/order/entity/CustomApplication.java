package ru.itmo.telecom.order.entity;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;
import lombok.NoArgsConstructor;
import lombok.AllArgsConstructor;

@Entity
@Table(name = "custom_applications")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class CustomApplication {

    // ID - это одновременно первичный ключ и внешний ключ к applications
    @Id
    private Integer id;

    // Ссылка на родительскую заявку (One-to-One)
    @OneToOne(fetch = FetchType.LAZY)
    @MapsId
    @JoinColumn(name = "application_id")
    private Application application;

    // Здесь можно добавить дополнительные поля, специфичные для конструктора,
    // например, итоговая скидка по промокоду или тип конструктора.
    // Пока оставим пустым.
}

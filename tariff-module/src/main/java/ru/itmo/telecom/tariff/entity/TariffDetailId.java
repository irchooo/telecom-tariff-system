package ru.itmo.telecom.tariff.entity;

import jakarta.persistence.Embeddable;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.Setter;
import lombok.NoArgsConstructor;
import lombok.AllArgsConstructor;

import java.io.Serializable;

// Аннотация @Embeddable говорит JPA, что этот класс может быть встроен в другую сущность
@Embeddable
@Getter
@Setter
@EqualsAndHashCode // Обязательно для сравнения и работы ключа
@NoArgsConstructor
@AllArgsConstructor
public class TariffDetailId implements Serializable {

    // Поле должно называться так же, как и поле в TariffDetail, которое оно маппит
    private Integer tariffId;
    private Integer parameterId;
}

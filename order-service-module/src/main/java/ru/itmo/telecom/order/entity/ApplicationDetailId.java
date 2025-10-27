package ru.itmo.telecom.order.entity;

import jakarta.persistence.Embeddable;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.AllArgsConstructor;
import lombok.Setter;

import java.io.Serializable;

@Embeddable
@Getter
@Setter
@EqualsAndHashCode // Обязательно для составного ключа
@NoArgsConstructor
@AllArgsConstructor
public class ApplicationDetailId implements Serializable {

    private Integer applicationId;
    private Integer parameterId;
}

package ru.itmo.telecom.order.entity;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;
import lombok.NoArgsConstructor;
import lombok.AllArgsConstructor;

import java.util.List;

@Entity
@Table(name = "custom_applications")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class CustomApplication {

    @Id
    private Integer id;

    // Ссылка на родительскую заявку (One-to-One)
    @OneToOne(fetch = FetchType.LAZY)
    @MapsId
    @JoinColumn(name = "application_id")
    private Application application;

    // ИСПРАВЛЕННАЯ связь с деталями заявки
    // ApplicationDetail ссылается на Application, а не на CustomApplication
    // Поэтому мы не можем использовать mappedBy здесь
    // Вместо этого создадим отдельный запрос для получения деталей
    @Transient // Помечаем как transient, чтобы Hibernate не пытался маппить это поле
    private List<ApplicationDetail> applicationDetails;

    // Конструктор без details для обратной совместимости
    public CustomApplication(Integer id, Application application) {
        this.id = id;
        this.application = application;
    }
}
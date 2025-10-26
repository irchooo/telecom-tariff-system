package ru.itmo.telecom.user.entity;

import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.Id;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Table;
import jakarta.persistence.Column;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.FetchType;
import lombok.Getter;
import lombok.Setter;
import org.hibernate.annotations.CreationTimestamp;

import java.time.Instant;

@Getter
@Setter
@Entity
@Table(name = "clients")
public class Client {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    @Column(name = "telegram_id", unique = true)
    private Long telegramId;

    @Column(name = "telegram_username", length = 32)
    private String telegramUsername;

    @Column(name = "first_name", nullable = false, length = 50)
    private String firstName;

    @Column(name = "last_name", nullable = false, length = 50)
    private String lastName;

    @Column(name = "phone_number", nullable = false, unique = true, length = 11)
    private String phoneNumber;

    @Column(name = "email", length = 100)
    private String email;

    @CreationTimestamp
    @Column(name = "registration_date", nullable = false, updatable = false)
    private Instant registrationDate;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "client_status_id", nullable = false)
    private ClientStatus status;

}
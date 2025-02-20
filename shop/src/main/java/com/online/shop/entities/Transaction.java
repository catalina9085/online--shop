package com.online.shop.entities;
import java.time.LocalDateTime;

import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.OneToOne;
import lombok.Getter;
import lombok.Setter;

@Entity
@Getter
@Setter
public class Transaction {
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Long id;

    @OneToOne
    private User user;

    private String paymentMethod; // PayPal, Credit Card, etc.
    private Double amount; // Suma tranzacției
    private String currency; // Moneda (ex. USD, EUR, etc.)
    private String status; // PENDING, COMPLETED, FAILED
    private String transactionId; // ID-ul generat de PayPal
    private LocalDateTime transactionDate; // Data tranzacției

}

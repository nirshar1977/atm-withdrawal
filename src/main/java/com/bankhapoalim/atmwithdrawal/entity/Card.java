package com.bankhapoalim.atmwithdrawal.entity;

import jakarta.persistence.*;
import lombok.Data;

@Entity
@Table(name = "card")
@Data
/**
 *  Represents a card associated with a bank account.
 *  It contains a foreign key referencing the BankAccount entity, establishing the relationship.
 */
public class Card {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "card_id")
    private long cardId;

    @Column(name = "card_number", nullable = false, unique = true, length = 16)
    private String cardNumber;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "account_id", nullable = false)
    private BankAccount bankAccount;
}

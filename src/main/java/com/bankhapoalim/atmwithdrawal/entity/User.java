package com.bankhapoalim.atmwithdrawal.entity;

import jakarta.persistence.*;
import lombok.Data;

@Entity
@Table(name = "user")
@Data
/**
 * Represents a user in the system
 */
public class User {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "user_id")
    private long userId;

    @Column(name = "username", unique = true, nullable = false)
    private String username;

    // Many users can be associated with one bank account
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "bank_account_id")
    private BankAccount bankAccount;
}


package com.bankhapoalim.atmwithdrawal.entity;

import jakarta.persistence.*;
import lombok.Data;

import java.math.BigDecimal;
import java.util.List;

@Entity
@Table(name = "bank_account")
@Data
public class BankAccount {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "account_id")
    private long accountId;

    @Column(name = "balance", precision = 10, scale = 2, nullable = false)
    private BigDecimal balance;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id", nullable = false)
    private User user;

    //Each BankAccount can have multiple transactions related to it
    @OneToMany(mappedBy = "bankAccount", cascade = CascadeType.ALL)
    private List<WithdrawalRequest> transactions;

    //Each BankAccount can have multiple cards connected to it
    @OneToMany(mappedBy = "bankAccount", cascade = CascadeType.ALL)
    private List<Card> cards;
}



package com.bankhapoalim.atmwithdrawal.entity;



import com.bankhapoalim.atmwithdrawal.enums.WithdrawalStatus;
import jakarta.persistence.*;
import lombok.Data;

import java.math.BigDecimal;

@Data
@Entity
@Table(name = "transaction")
/**
 * Represents a transaction related to a bank account
 */
public class WithdrawalRequest {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "transaction_id")
    private long transactionId;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "account_id", nullable = false)
    private BankAccount  bankAccount;

    @Column(name = "card_number", length = 16, nullable = false)
    private String cardNumber;

    @Column(name = "secret_code", length = 4, nullable = false)
    private String secretCode;

    @Column(name = "amount", precision = 10, scale = 2, nullable = false)
    private BigDecimal amount;

    @Enumerated(EnumType.STRING)
    @Column(name = "status")
    private WithdrawalStatus status;
}


package com.bankhapoalim.atmwithdrawal.entity;

import jakarta.persistence.*;
import lombok.Data;

@Entity
@Table(name = "transaction_history")
@Data
/**
 * Represents the history of transactions.
 */
public class TransactionHistory extends BaseEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "history_id")
    private Long historyId;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "withdrawal_request_id", nullable = false)
    private WithdrawalRequest withdrawalRequest;
}



package com.bankhapoalim.atmwithdrawal.entity;

import jakarta.persistence.*;
import lombok.Data;

@Entity
@Table(name = "audit_log")
@Data
/**
 * Represents an audit log related to transactions
 */
public class AuditLog extends BaseEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "log_id")
    private Long logId;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "withdrawal_request_id", nullable = false)
    private WithdrawalRequest withdrawalRequest;
}


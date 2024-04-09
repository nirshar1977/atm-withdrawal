package com.bankhapoalim.atmwithdrawal.repository;

import com.bankhapoalim.atmwithdrawal.entity.WithdrawalRequest;
import org.springframework.data.jpa.repository.JpaRepository;

import java.time.LocalDate;
import java.util.List;

/**
 * Repository interface for managing WithdrawalRequest entities.
 * Provides CRUD operations and custom query methods for interacting with the WithdrawalRequest entity.
 */
public interface WithdrawalRequestRepository extends JpaRepository<WithdrawalRequest, Long> {
}


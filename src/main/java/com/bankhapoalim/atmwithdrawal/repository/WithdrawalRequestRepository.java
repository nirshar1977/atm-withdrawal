package com.bankhapoalim.atmwithdrawal.repository;

import com.bankhapoalim.atmwithdrawal.entity.WithdrawalRequest;
import org.springframework.data.jpa.repository.JpaRepository;

public interface WithdrawalRequestRepository extends JpaRepository<WithdrawalRequest, Long> {
    // Add custom query methods if needed
}


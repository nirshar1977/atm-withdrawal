package com.bankhapoalim.atmwithdrawal.service;

import com.bankhapoalim.atmwithdrawal.dto.WithdrawalRequestDTO;

public interface WithdrawalService {
    boolean processWithdrawalRequest(WithdrawalRequestDTO withdrawalRequestDTO);

    boolean cancelWithdrawalRequest(Long withdrawalRequestId);
}


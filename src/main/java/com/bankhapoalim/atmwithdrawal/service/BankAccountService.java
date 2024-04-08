package com.bankhapoalim.atmwithdrawal.service;

import com.bankhapoalim.atmwithdrawal.entity.BankAccount;
import com.bankhapoalim.atmwithdrawal.entity.Card;

import java.math.BigDecimal;

public interface BankAccountService {
    BankAccount getAccountFromCache(Card card);
    void reverseBalance(Long accountId, BigDecimal amountToAdd, String reason);
}

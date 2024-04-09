package com.bankhapoalim.atmwithdrawal.service;

import com.bankhapoalim.atmwithdrawal.entity.BankAccount;
import com.bankhapoalim.atmwithdrawal.entity.WithdrawalRequest;
import com.bankhapoalim.atmwithdrawal.enums.WithdrawalRequestStatus;

import java.math.BigDecimal;

public class WithdrawalRequestBuilder {
    private String cardNumber;
    private String secretCode;
    private BigDecimal amount;
    private BankAccount account;
    private WithdrawalRequestStatus status;

    public WithdrawalRequestBuilder setCardNumber(String cardNumber) {
        this.cardNumber = cardNumber;
        return this;
    }

    public WithdrawalRequestBuilder setSecretCode(String secretCode) {
        this.secretCode = secretCode;
        return this;
    }

    public WithdrawalRequestBuilder setAmount(BigDecimal amount) {
        this.amount = amount;
        return this;
    }

    public WithdrawalRequestBuilder setBankAccount(BankAccount bankAccount) {
        this.account = bankAccount;
        return this;
    }

    public WithdrawalRequestBuilder setWithdrawalStatus(WithdrawalRequestStatus withdrawalStatus) {
        this.status = withdrawalStatus;
        return this;
    }

    public WithdrawalRequest build() {
        WithdrawalRequest withdrawalRequest = new WithdrawalRequest();
        withdrawalRequest.setCardNumber(cardNumber);
        withdrawalRequest.setSecretCode(secretCode);
        withdrawalRequest.setAmount(amount);
        withdrawalRequest.setBankAccount(account);
        return withdrawalRequest;
    }
}


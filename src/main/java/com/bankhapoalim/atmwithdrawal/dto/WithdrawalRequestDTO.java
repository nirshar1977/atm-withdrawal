package com.bankhapoalim.atmwithdrawal.dto;

import lombok.Data;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.Positive;

@Data
public class WithdrawalRequestDTO {
    @NotBlank(message = "Card number must not be blank")
    private String cardNumber;
    @NotBlank(message = "Secret code must not be blank")
    private String secretCode;
    @Positive(message = "Amount must be positive")
    private double amount;
}
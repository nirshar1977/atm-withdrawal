package com.bankhapoalim.atmwithdrawal.dto;

import lombok.Data;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Positive;
import javax.validation.constraints.PositiveOrZero;
import java.math.BigDecimal;

@Data
public class WithdrawalRequestDTO {
    @NotBlank(message = "Card number is required")
    private String cardNumber;

    @NotBlank(message = "Secret code is required")
    private String secretCode;

    @NotNull(message = "Amount is required")
    @PositiveOrZero(message = "Amount must be a positive value or zero")
    private BigDecimal amount;
}
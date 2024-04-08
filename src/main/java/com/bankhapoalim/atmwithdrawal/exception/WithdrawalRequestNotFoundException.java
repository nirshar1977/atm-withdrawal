package com.bankhapoalim.atmwithdrawal.exception;

public class WithdrawalRequestNotFoundException extends RuntimeException {

    public WithdrawalRequestNotFoundException() {
        super();
    }

    public WithdrawalRequestNotFoundException(String message) {
        super(message);
    }

    public WithdrawalRequestNotFoundException(String message, Throwable cause) {
        super(message, cause);
    }

    public WithdrawalRequestNotFoundException(Throwable cause) {
        super(cause);
    }
}


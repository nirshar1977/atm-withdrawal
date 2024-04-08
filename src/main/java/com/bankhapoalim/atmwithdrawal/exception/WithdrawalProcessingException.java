package com.bankhapoalim.atmwithdrawal.exception;

public class WithdrawalProcessingException extends RuntimeException {

    public WithdrawalProcessingException() {
        super();
    }

    public WithdrawalProcessingException(String message) {
        super(message);
    }

    public WithdrawalProcessingException(String message, Throwable cause) {
        super(message, cause);
    }
}


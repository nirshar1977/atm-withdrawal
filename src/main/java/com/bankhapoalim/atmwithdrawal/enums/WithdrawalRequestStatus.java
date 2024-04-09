package com.bankhapoalim.atmwithdrawal.enums;

public enum WithdrawalRequestStatus {
    PENDING("Pending"),
    CANCELED("Canceled"),
    IN_PROGRESS("In Progress"),
    COMPLETED("Completed");

    private final String displayName;

    WithdrawalRequestStatus(String displayName) {
        this.displayName = displayName;
    }

    public String getDisplayName() {
        return displayName;
    }
}


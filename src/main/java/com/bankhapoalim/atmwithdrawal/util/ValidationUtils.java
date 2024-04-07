package com.bankhapoalim.atmwithdrawal.util;

import com.bankhapoalim.atmwithdrawal.dto.WithdrawalRequestDTO;
import com.bankhapoalim.atmwithdrawal.entity.BankAccount;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import java.time.LocalDateTime;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.function.Predicate;

/**
 * Utility class containing validation methods for ATM withdrawal requests.
 * Handles validation of withdrawal request parameters, daily withdrawal limits, and duplicate requests.
 *
 * Note: This class is managed as a singleton bean by Spring Boot, meaning that there is only one
 * instance shared across the application. The map variables within this class are shared among all
 * parts of the application that use the ValidationUtils bean.
 */
@Component
@Slf4j
public class ValidationUtils {

    // Read the threshold value from application properties
    @Value("${duplicate.threshold.minutes}")
    //This variable holds the value of duplicate.threshold.minutes,
    // which represents the time threshold for duplicate requests in minutes.
    private int duplicateThresholdMinutes;

    private static final int MAX_DAILY_WITHDRAWALS = 5;
    private static final double MAX_DAILY_WITHDRAWAL_AMOUNT = 2000.0;

    private final Map<String, LocalDateTime> lastWithdrawalTimes = new ConcurrentHashMap<>();
    private final Map<String, Double> dailyWithdrawalAmounts = new ConcurrentHashMap<>();
    private final Map<String, AtomicInteger> dailyWithdrawalCounts = new ConcurrentHashMap<>();
    private final Map<String, LocalDateTime> lastWithdrawalTimestamps = new ConcurrentHashMap<>();

    public Predicate<WithdrawalRequestDTO> isValidWithdrawalRequest() {
        return isRequestNotNull()
                .and(hasValidCardNumber())
                .and(hasValidSecretCode())
                .and(hasValidWithdrawalAmount())
                .and(isWithinDailyWithdrawalLimit())
                .and(isNotDuplicateWithdrawalRequest());
    }

    private Predicate<WithdrawalRequestDTO> isRequestNotNull() {
        return withdrawalRequest -> {
            if (withdrawalRequest == null) {
                log.error("Withdrawal request is null");
                return false;
            }
            return true;
        };
    }

    private Predicate<WithdrawalRequestDTO> hasValidCardNumber() {
        final int VALID_CARD_NUMBER_LENGTH = 16;
        return withdrawalRequest -> {
            if (withdrawalRequest.getCardNumber() == null || withdrawalRequest.getCardNumber().length() != VALID_CARD_NUMBER_LENGTH) {
                log.error("Invalid card number format: {}", withdrawalRequest.getCardNumber());
                return false;
            }
            return true;
        };
    }

    private Predicate<WithdrawalRequestDTO> hasValidSecretCode() {
        final int VALID_SECRET_CODE_LENGTH = 4;
        return withdrawalRequest -> {
            if (withdrawalRequest.getSecretCode() == null || withdrawalRequest.getSecretCode().length() != VALID_SECRET_CODE_LENGTH) {
                log.error("Invalid secret code format: {}", withdrawalRequest.getSecretCode());
                return false;
            }
            return true;
        };
    }

    private Predicate<WithdrawalRequestDTO> hasValidWithdrawalAmount() {
        return withdrawalRequest -> {
            if (withdrawalRequest.getAmount().intValue() <= 0) {
                log.error("Invalid withdrawal amount: {}", withdrawalRequest.getAmount());
                return false;
            }
            return true;
        };
    }


    /**
     * Checks if the given withdrawal request is within the daily withdrawal limit for the customer.
     * Updates the daily withdrawal amount and count for the customer and ensures that the total
     * daily withdrawals and the number of withdrawals do not exceed the specified limits.
     *
     * @return true if the withdrawal request is within the daily withdrawal limit, false otherwise.
     */
    private synchronized Predicate<WithdrawalRequestDTO> isWithinDailyWithdrawalLimit() {
        return withdrawalRequest -> {
            String cardNumber = withdrawalRequest.getCardNumber();
            double amount = withdrawalRequest.getAmount().intValue();

            LocalDateTime currentTime = LocalDateTime.now();
            LocalDateTime lastWithdrawalTime = lastWithdrawalTimes.getOrDefault(cardNumber, null);
            double dailyWithdrawalAmount = dailyWithdrawalAmounts.getOrDefault(cardNumber, 0.0);
            AtomicInteger dailyWithdrawalCount = dailyWithdrawalCounts.computeIfAbsent(cardNumber, k -> new AtomicInteger());

            if (lastWithdrawalTime == null || lastWithdrawalTime.toLocalDate().isBefore(currentTime.toLocalDate())) {
                dailyWithdrawalAmounts.put(cardNumber, amount);
                dailyWithdrawalCount.set(1);
                lastWithdrawalTimes.put(cardNumber, currentTime);
            } else if (dailyWithdrawalAmount + amount <= MAX_DAILY_WITHDRAWAL_AMOUNT
                    && dailyWithdrawalCount.incrementAndGet() <= MAX_DAILY_WITHDRAWALS) {
                dailyWithdrawalAmounts.put(cardNumber, dailyWithdrawalAmount + amount);
                lastWithdrawalTimes.put(cardNumber, currentTime);
            } else {
                log.error("Exceeded daily withdrawal limit or count for customer: {}", cardNumber);
                return false;
            }

            return true;
        };
    }


    /**
     * Checks if the given withdrawal request is not a duplicate request within a specified time threshold.
     * Prevents duplicate requests for the same card number within the defined time threshold.
     *
     * @return Predicate representing the validation logic for duplicate withdrawal requests.
     */
    private synchronized Predicate<WithdrawalRequestDTO> isNotDuplicateWithdrawalRequest() {
        return withdrawalRequest -> {
            String cardNumber = withdrawalRequest.getCardNumber();
            LocalDateTime currentTime = LocalDateTime.now();
            LocalDateTime lastTimestamp = lastWithdrawalTimestamps.getOrDefault(cardNumber, null);

            if (lastTimestamp != null && currentTime.minusMinutes(duplicateThresholdMinutes).isBefore(lastTimestamp)) {
                log.error("Duplicate withdrawal request detected for card number: {}", cardNumber);
                return false;
            }

            lastWithdrawalTimestamps.put(cardNumber, currentTime);
            return true;
        };
    }

}
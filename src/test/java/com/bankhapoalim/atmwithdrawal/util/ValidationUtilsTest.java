package com.bankhapoalim.atmwithdrawal.util;

import com.bankhapoalim.atmwithdrawal.dto.WithdrawalRequestDTO;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Disabled;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.TestPropertySource;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;

@ActiveProfiles("test")
@TestPropertySource(locations = "application-test.properties")
class ValidationUtilsTest {
    private ValidationUtils validationUtils;

    @BeforeEach
    void setUp() {
        validationUtils = new ValidationUtils();
    }

    @Test
    void isValidWithdrawalRequest_NullRequest_False() {
        boolean result = validationUtils.isValidWithdrawalRequest().test(null);
        Assertions.assertFalse(result);
    }

    @Test
    void isValidWithdrawalRequest_InvalidCardNumber_False() {
        WithdrawalRequestDTO withdrawalRequestDTO = new WithdrawalRequestDTO();
        withdrawalRequestDTO.setCardNumber("123"); // Invalid card number length

        boolean result = validationUtils.isValidWithdrawalRequest().test(withdrawalRequestDTO);
        Assertions.assertFalse(result);
    }

    @Test
    void isValidWithdrawalRequest_InvalidSecretCode_False() {
        WithdrawalRequestDTO withdrawalRequestDTO = new WithdrawalRequestDTO();
        withdrawalRequestDTO.setCardNumber("1234567890123456");
        withdrawalRequestDTO.setSecretCode("123"); // Invalid secret code length

        boolean result = validationUtils.isValidWithdrawalRequest().test(withdrawalRequestDTO);
        Assertions.assertFalse(result);
    }

    @Test
    void isValidWithdrawalRequest_InvalidWithdrawalAmount_False() {
        WithdrawalRequestDTO withdrawalRequestDTO = new WithdrawalRequestDTO();
        withdrawalRequestDTO.setCardNumber("1234567890123456");
        withdrawalRequestDTO.setSecretCode("1234");
        withdrawalRequestDTO.setAmount(BigDecimal.valueOf(-100)); // Negative withdrawal amount

        boolean result = validationUtils.isValidWithdrawalRequest().test(withdrawalRequestDTO);
        Assertions.assertFalse(result);
    }

    @Test
    void isValidWithdrawalRequest_ValidRequest_True() {
        WithdrawalRequestDTO withdrawalRequestDTO = new WithdrawalRequestDTO();
        withdrawalRequestDTO.setCardNumber("1234567890123456");
        withdrawalRequestDTO.setSecretCode("1234");
        withdrawalRequestDTO.setAmount(BigDecimal.valueOf(100));

        boolean result = validationUtils.isValidWithdrawalRequest().test(withdrawalRequestDTO);
        Assertions.assertTrue(result);
    }

    @Test
    void isWithinDailyWithdrawalLimit_ExceedDailyWithdrawalLimit_False() {
        WithdrawalRequestDTO withdrawalRequestDTO = new WithdrawalRequestDTO();
        withdrawalRequestDTO.setCardNumber("1234567890123456");
        withdrawalRequestDTO.setAmount(BigDecimal.valueOf(2500)); // Assuming daily limit is 2000

        boolean result = validationUtils.isWithinDailyWithdrawalLimit().test(withdrawalRequestDTO);
        Assertions.assertFalse(result);
    }

    @Test
    void isWithinDailyWithdrawalLimit_ValidRequest_True() {
        WithdrawalRequestDTO withdrawalRequestDTO = new WithdrawalRequestDTO();
        withdrawalRequestDTO.setCardNumber("1234567890123456");
        withdrawalRequestDTO.setAmount(BigDecimal.valueOf(500)); // Assuming daily limit is 2000

        // Simulate a valid request by setting the last withdrawal time as the previous day
        validationUtils.getLastWithdrawalTimes().put("1234567890123456", LocalDate.now().minusDays(1).atStartOfDay());

        boolean result = validationUtils.isWithinDailyWithdrawalLimit().test(withdrawalRequestDTO);
        Assertions.assertTrue(result);
    }

    @Disabled
    @Test
    void isNotDuplicateWithdrawalRequest_DuplicateRequest_False() {
        WithdrawalRequestDTO withdrawalRequestDTO = new WithdrawalRequestDTO();
        withdrawalRequestDTO.setCardNumber("1234567890123456");

        // Simulate a previous withdrawal within the duplicate threshold time
        validationUtils.getLastWithdrawalTimestamps().put("1234567890123456", LocalDate.now().atStartOfDay());

        boolean result = validationUtils.isNotDuplicateWithdrawalRequest().test(withdrawalRequestDTO);
        Assertions.assertFalse(result);
    }

    @Test
    void isNotDuplicateWithdrawalRequest_ValidRequest_True() {
        WithdrawalRequestDTO withdrawalRequestDTO = new WithdrawalRequestDTO();
        withdrawalRequestDTO.setCardNumber("1234567890123456");

        // Simulate a previous withdrawal outside the duplicate threshold time
        validationUtils.getLastWithdrawalTimestamps().put("1234567890123456", LocalDateTime.now().minusMinutes(15));


        boolean result = validationUtils.isNotDuplicateWithdrawalRequest().test(withdrawalRequestDTO);
        Assertions.assertTrue(result);
    }
}

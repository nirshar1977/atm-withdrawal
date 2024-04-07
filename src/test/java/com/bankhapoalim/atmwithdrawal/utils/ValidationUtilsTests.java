package com.bankhapoalim.atmwithdrawal.utils;

import com.bankhapoalim.atmwithdrawal.dto.WithdrawalRequestDTO;
import com.bankhapoalim.atmwithdrawal.util.ValidationUtils;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.springframework.boot.test.context.SpringBootTest;

import java.math.BigDecimal;
import java.time.LocalDateTime;

import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

@SpringBootTest
public class ValidationUtilsTests {

    @Mock
    private WithdrawalRequestDTO withdrawalRequestDTO;

    @InjectMocks
    private ValidationUtils validationUtils;

    @Test
    public void testIsWithinDailyWithdrawalLimit_ValidWithdrawal() {
        Mockito.when(withdrawalRequestDTO.getCardNumber()).thenReturn("1234567890123456");
        Mockito.when(withdrawalRequestDTO.getAmount()).thenReturn(BigDecimal.valueOf(1000.0));

        boolean result = validationUtils.isWithinDailyWithdrawalLimit().test(withdrawalRequestDTO);

        assertTrue(result);
    }

    @Test
    public void testIsWithinDailyWithdrawalLimit_ExceedDailyLimit() {
        Mockito.when(withdrawalRequestDTO.getCardNumber()).thenReturn("1234567890123456");
        Mockito.when(withdrawalRequestDTO.getAmount()).thenReturn(BigDecimal.valueOf(3000.0));

        boolean result = validationUtils.isWithinDailyWithdrawalLimit().test(withdrawalRequestDTO);

        assertFalse(result);
    }

    @Test
    public void testIsNotDuplicateWithdrawalRequest_NotDuplicate() {
        Mockito.when(withdrawalRequestDTO.getCardNumber()).thenReturn("1234567890123456");
        Mockito.when(withdrawalRequestDTO.getAmount()).thenReturn(BigDecimal.valueOf(1000.0));

        boolean result1 = validationUtils.isNotDuplicateWithdrawalRequest().test(withdrawalRequestDTO);
        boolean result2 = validationUtils.isNotDuplicateWithdrawalRequest().test(withdrawalRequestDTO);

        assertTrue(result1);
        assertTrue(result2);
    }

    @Test
    public void testIsNotDuplicateWithdrawalRequest_Duplicate() {
        Mockito.when(withdrawalRequestDTO.getCardNumber()).thenReturn("1234567890123456");
        Mockito.when(withdrawalRequestDTO.getAmount()).thenReturn(BigDecimal.valueOf(1000.0));

        boolean result1 = validationUtils.isNotDuplicateWithdrawalRequest().test(withdrawalRequestDTO);

        // Simulate a duplicate request within the threshold time
        LocalDateTime currentTime = LocalDateTime.now();
        LocalDateTime lastTimestamp = currentTime.minusMinutes(5); // Assume a 5-minute threshold

        boolean result2 = validationUtils.isNotDuplicateWithdrawalRequest().test(withdrawalRequestDTO);

        assertTrue(result1);
        assertFalse(result2);
    }
}



package com.bankhapoalim.atmwithdrawal.service;

import com.bankhapoalim.atmwithdrawal.entity.BankAccount;
import com.bankhapoalim.atmwithdrawal.repository.BankAccountRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@Service
class BankAccountServiceImplTest {
    @Mock
    private BankAccountRepository bankAccountRepository;

    @InjectMocks
    private BankAccountServiceImpl bankAccountService;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
    }

    @Test
    void testReverseBalance_NonNullBankAccount() {
        // Mock data
        BankAccount bankAccount = new BankAccount();
        bankAccount.setAccountId(123L);
        bankAccount.setBalance(BigDecimal.valueOf(1000.0));

        BigDecimal amountToAdd = BigDecimal.valueOf(50.0);
        String reason = "Withdrawal cancellation";

        // Mock bankAccountRepository.save() to return the same bankAccount object
        when(bankAccountRepository.save(any(BankAccount.class))).thenReturn(bankAccount);

        // Call the method
        bankAccountService.reverseBalance(bankAccount, amountToAdd, reason);

        // Verify that balance is updated correctly
        BigDecimal expectedBalance = BigDecimal.valueOf(1050.0);
        assertEquals(expectedBalance, bankAccount.getBalance());
    }

    @Test
    void testReverseBalance_NullBankAccount() {
        // Call the method with null bankAccount
        assertThrows(IllegalArgumentException.class,
                () -> bankAccountService.reverseBalance(null, BigDecimal.valueOf(50.0), "Withdrawal cancellation"));
    }

    @Test
    void testReverseBalance_Success() {
        // Create a mock BankAccount object with an initial balance
        BankAccount bankAccount = new BankAccount();
        BigDecimal initialBalance = BigDecimal.valueOf(1000.0);
        bankAccount.setBalance(initialBalance);

        // Set the amount to add back to the balance (simulate reversal)
        BigDecimal amountToAdd = BigDecimal.valueOf(50.0);

        // Call the reverseBalance method
        bankAccountService.reverseBalance(bankAccount, amountToAdd, "Withdrawal cancellation");

        // Verify that the updated balance is correct
        BigDecimal expectedBalance = initialBalance.add(amountToAdd);
        assertEquals(expectedBalance, bankAccount.getBalance());

        // Verify that the save method was called on the bankAccountRepository
        verify(bankAccountRepository, times(1)).save(bankAccount);

        // Verify no other interactions with bankAccountRepository
        verifyNoMoreInteractions(bankAccountRepository);
    }
}
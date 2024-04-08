package com.bankhapoalim.atmwithdrawal.service;

import com.bankhapoalim.atmwithdrawal.dto.WithdrawalRequestDTO;
import com.bankhapoalim.atmwithdrawal.entity.BankAccount;
import com.bankhapoalim.atmwithdrawal.entity.WithdrawalRequest;
import com.bankhapoalim.atmwithdrawal.enums.WithdrawalStatus;
import com.bankhapoalim.atmwithdrawal.repository.BankAccountRepository;
import com.bankhapoalim.atmwithdrawal.repository.WithdrawalRequestRepository;
import com.bankhapoalim.atmwithdrawal.util.ValidationUtils;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import java.math.BigDecimal;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

class WithdrawalServiceImplTest {

    @Mock
    private ValidationUtils validationUtils;

    @Mock
    private WithdrawalRequestRepository withdrawalRequestRepository;

    @Mock
    private BankAccountRepository accountRepository;

    @Mock
    private BankAccountServiceImpl bankAccountService;

    @InjectMocks
    private WithdrawalServiceImpl withdrawalService;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
    }

    @Test
    void testProcessWithdrawalRequest_Success() {
        // Mocking the validation check to return true
        when(validationUtils.isValidWithdrawalRequest()).thenReturn(withdrawalRequestDTO -> true);

        WithdrawalRequestDTO withdrawalRequestDTO = new WithdrawalRequestDTO();
        withdrawalRequestDTO.setCardNumber("1234567890123456");
        withdrawalRequestDTO.setSecretCode("1234");
        withdrawalRequestDTO.setAmount(BigDecimal.valueOf(100.0));

        BankAccount mockBankAccount = new BankAccount();
        mockBankAccount.setAccountId(1L);
        mockBankAccount.setBalance(BigDecimal.valueOf(1000.0));

        // Mock the bankAccountService to return the mockBankAccount
        when(bankAccountService.getAccountFromCache(any())).thenReturn(mockBankAccount);

        // Perform the method call
        boolean result = withdrawalService.processWithdrawalRequest(withdrawalRequestDTO);

        // Verify that the save method was called with the correct parameters
        verify(withdrawalRequestRepository, times(1)).save(any());
        verify(accountRepository, times(1)).save(any());

        // Assert the result
        assert(result);
    }

    @Test
    void testProcessWithdrawalRequest_InvalidRequest() {
        // Mocking the validation check to return false (indicating an invalid withdrawal request)
        when(validationUtils.isValidWithdrawalRequest()).thenReturn(withdrawalRequestDTO -> false);

        WithdrawalRequestDTO withdrawalRequestDTO = new WithdrawalRequestDTO();
        withdrawalRequestDTO.setCardNumber("1234567890123456");
        withdrawalRequestDTO.setSecretCode("1234");
        withdrawalRequestDTO.setAmount(BigDecimal.valueOf(100.0));

        // Perform the method call
        boolean result = withdrawalService.processWithdrawalRequest(withdrawalRequestDTO);

        // Verify that the save method was not called (since the request is invalid)
        verify(withdrawalRequestRepository, never()).save(any());
        verify(accountRepository, never()).save(any());

        // Assert that the method returned false
        assert(!result);
    }

    @Test
    void testProcessWithdrawalRequest_InsufficientBalance() {
        // Mock validation to return true (indicating a valid withdrawal request)
        when(validationUtils.isValidWithdrawalRequest()).thenReturn(withdrawalRequestDTO -> true);

        WithdrawalRequestDTO withdrawalRequestDTO = new WithdrawalRequestDTO();
        withdrawalRequestDTO.setCardNumber("1234567890123456");
        withdrawalRequestDTO.setSecretCode("1234");

        // Set an amount that exceeds the current balance
        BigDecimal currentBalance = BigDecimal.valueOf(50.0);
        BankAccount bankAccount = new BankAccount();
        bankAccount.setBalance(currentBalance);

        // Mock the bankAccountService to return the mockBankAccount
        when(bankAccountService.getAccountFromCache(any())).thenReturn(bankAccount);
        when(accountRepository.save(any(BankAccount.class))).thenReturn(bankAccount);
        when(accountRepository.findById(anyLong())).thenReturn(java.util.Optional.of(bankAccount));


        // Set an amount greater than the current balance
        BigDecimal withdrawalAmount = BigDecimal.valueOf(100.0);
        withdrawalRequestDTO.setAmount(withdrawalAmount);

        // Perform the method call
        assertThrows(IllegalArgumentException.class, () -> withdrawalService.processWithdrawalRequest(withdrawalRequestDTO));

        // Verify that the account balance was not updated (since the balance is insufficient)
        verify(accountRepository, never()).save(any());
    }

    @Test
    void testCancelWithdrawalRequest_Completed() {
        // Mock a completed withdrawal request
        WithdrawalRequest withdrawalRequest = new WithdrawalRequest();
        withdrawalRequest.setStatus(WithdrawalStatus.COMPLETED);
        BigDecimal withdrawalAmount = BigDecimal.valueOf(50.0);
        withdrawalRequest.setAmount(withdrawalAmount);

        // Mock a bank account associated with the withdrawal request
        BankAccount bankAccount = new BankAccount();
        BigDecimal initialBalance = BigDecimal.valueOf(1000.0); // Assuming initial balance is $1000
        bankAccount.setBalance(initialBalance);
        long accountId = 123L; // Example account ID
        bankAccount.setAccountId(accountId);
        withdrawalRequest.setBankAccount(bankAccount);

        // Mock the findById methods of withdrawalRequestRepository and bankAccountRepository
        when(withdrawalRequestRepository.findById(anyLong())).thenReturn(Optional.of(withdrawalRequest));

        // Mock the reverseBalance method of bankAccountService
        doNothing().when(bankAccountService).reverseBalance(eq(accountId), any(BigDecimal.class), eq("Withdrawal cancellation"));

        // Perform the cancellation
        boolean cancellationResult = withdrawalService.cancelWithdrawalRequest(123L);

        // Verify that the cancellation is successful
        assertTrue(cancellationResult); // Assuming your cancelWithdrawalRequest method returns a boolean indicating success

        // Verify that the withdrawal status is updated to CANCELED
        assertEquals(WithdrawalStatus.CANCELED, withdrawalRequest.getStatus());

        // Verify that the amount is added back to the account balance
        BigDecimal expectedBalance = initialBalance.add(withdrawalAmount);
        assertEquals(expectedBalance, bankAccount.getBalance());

        // Verify interactions with the repositories/services
        verify(withdrawalRequestRepository, times(1)).findById(123L);
        verify(withdrawalRequestRepository, times(1)).save(withdrawalRequest);
        verify(accountRepository, times(1)).findById(accountId);
    }
}
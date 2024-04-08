package com.bankhapoalim.atmwithdrawal.service;

import com.bankhapoalim.atmwithdrawal.dto.WithdrawalRequestDTO;
import com.bankhapoalim.atmwithdrawal.entity.BankAccount;
import com.bankhapoalim.atmwithdrawal.repository.BankAccountRepository;
import com.bankhapoalim.atmwithdrawal.repository.WithdrawalRequestRepository;
import com.bankhapoalim.atmwithdrawal.util.ValidationUtils;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import java.math.BigDecimal;

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
}
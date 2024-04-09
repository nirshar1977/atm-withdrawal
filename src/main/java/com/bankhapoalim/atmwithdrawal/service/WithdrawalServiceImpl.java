package com.bankhapoalim.atmwithdrawal.service;

import com.bankhapoalim.atmwithdrawal.dto.WithdrawalRequestDTO;
import com.bankhapoalim.atmwithdrawal.entity.BankAccount;
import com.bankhapoalim.atmwithdrawal.entity.Card;
import com.bankhapoalim.atmwithdrawal.entity.WithdrawalRequest;
import com.bankhapoalim.atmwithdrawal.enums.WithdrawalRequestStatus;
import com.bankhapoalim.atmwithdrawal.exception.AccountNotFoundException;
import com.bankhapoalim.atmwithdrawal.exception.WithdrawalProcessingException;
import com.bankhapoalim.atmwithdrawal.exception.WithdrawalRequestNotFoundException;
import com.bankhapoalim.atmwithdrawal.repository.BankAccountRepository;
import com.bankhapoalim.atmwithdrawal.repository.WithdrawalRequestRepository;
import com.bankhapoalim.atmwithdrawal.util.ValidationUtils;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.util.Optional;

@Service
@Slf4j
public class WithdrawalServiceImpl implements WithdrawalService{

    @Autowired
    private ValidationUtils validationUtils;

    @Autowired
    private WithdrawalRequestRepository withdrawalRequestRepository;

    @Autowired
    private BankAccountRepository accountRepository;

    @Autowired
    private BankAccountServiceImpl bankAccountService;

    /**
     * Process a withdrawal request based on the provided data.
     *
     * @param withdrawalRequestDTO The withdrawal request details including card number, secret code, and amount.
     * @return true if the withdrawal request is processed successfully, false otherwise.
     */
    @Override
    public boolean processWithdrawalRequest(WithdrawalRequestDTO withdrawalRequestDTO) {
        if (!validationUtils.isValidWithdrawalRequest().test(withdrawalRequestDTO)) {
            log.error("Invalid withdrawal request: {}", withdrawalRequestDTO);
            return false;
        }

        BankAccount bankAccount = updateAccountBalance(withdrawalRequestDTO);

        WithdrawalRequest withdrawalRequest = new WithdrawalRequestBuilder()
                .setCardNumber(withdrawalRequestDTO.getCardNumber())
                .setSecretCode(withdrawalRequestDTO.getSecretCode())
                .setAmount(withdrawalRequestDTO.getAmount())
                .setBankAccount(bankAccount)
                .setWithdrawalStatus(WithdrawalRequestStatus.IN_PROGRESS)
                .build();

        try {
            withdrawalRequestRepository.save(withdrawalRequest);
            withdrawalRequest.setStatus(WithdrawalRequestStatus.COMPLETED);
            log.info("Withdrawal request processed successfully: {}", withdrawalRequest);
        } catch (Exception e) {
            log.error("Failed to process withdrawal request: {}", e.getMessage());
            throw new WithdrawalProcessingException("Error processing withdrawal request", e);
        }

        return true;
    }

    /**
     * Updates the account balance by deducting the withdrawal amount and saves the updated balance to the database.
     * If the withdrawal amount exceeds the current balance, an IllegalArgumentException is thrown.
     *
     * @param withdrawalRequestDTO The WithdrawalRequestDTO containing the withdrawal details.
     * @return The BankAccount object with the updated balance.
     * @throws IllegalArgumentException If the withdrawal amount exceeds the current balance or the bank account is not found.
     */
    private BankAccount updateAccountBalance(WithdrawalRequestDTO withdrawalRequestDTO) {
        // Initialize a Card object with the card number
        Card card = new Card();
        card.setCardNumber(withdrawalRequestDTO.getCardNumber());

        // Calculate new balance
        BankAccount bankAccount = bankAccountService.getAccountFromCacheByCardNumber(card);
        if (bankAccount == null) {
            throw new AccountNotFoundException("Bank Account not found for card: " + card.getCardNumber());
        }

        BigDecimal withdrawalAmount = withdrawalRequestDTO.getAmount();
        BigDecimal updatedBalance = calculateUpdatedBalance(bankAccount, withdrawalAmount);

        // Update account balance
        bankAccount.setBalance(updatedBalance);
        accountRepository.save(bankAccount);
        return bankAccount;
    }

    /**
     * Calculates the updated balance after deducting the withdrawal amount from the current balance.
     * If the withdrawal amount exceeds the current balance, an IllegalArgumentException is thrown.
     *
     * @param bankAccount     The BankAccount object representing the user's account.
     * @param withdrawalAmount The amount to be withdrawn from the account.
     * @return The updated balance after deducting the withdrawal amount.
     * @throws IllegalArgumentException If the withdrawal amount exceeds the current balance.
     */
    private BigDecimal calculateUpdatedBalance(BankAccount bankAccount, BigDecimal withdrawalAmount) {
        BigDecimal currentBalance = bankAccount.getBalance();

        // Check if withdrawal amount exceeds the current balance
        if (withdrawalAmount.compareTo(currentBalance) > 0) {
            throw new IllegalArgumentException("Insufficient balance");
        }

        return currentBalance.subtract(withdrawalAmount);
    }


    /**
     * Cancel a withdrawal request that was not completed.
     *  Steps to consider:
     *      Audit Logging: Log the cancellation event for auditing purposes. This can include details such as who cancelled the request, timestamp, and the reason for cancellation.
     *      Update Account Balance: If the withdrawal amount has already been deducted from the account balance, you may need to update the account balance to reverse the deduction.
     *      Validation Checks: Perform additional checks before allowing cancellation, such as checking if the withdrawal request is still in a cancellable state, verifying the user's authorization to cancel, etc.
     *
     * @param withdrawalRequestId The ID of the withdrawal request to cancel.
     * @return true if the cancellation is successful, false otherwise.
     */
    @Override
    public boolean cancelWithdrawalRequest(Long withdrawalRequestId) {
        boolean res = false;
        Optional<WithdrawalRequest> withdrawalRequestOptional = withdrawalRequestRepository.findById(withdrawalRequestId);
        if (withdrawalRequestOptional.isPresent()) {
            WithdrawalRequest withdrawalRequest = withdrawalRequestOptional.get();
            switch(withdrawalRequest.getStatus()){
                case COMPLETED -> {
                    BigDecimal amountToReverse = withdrawalRequest.getAmount();
                    bankAccountService.reverseBalance(withdrawalRequest.getBankAccount(), amountToReverse, "Withdrawal cancellation");
                    withdrawalRequest.setStatus(WithdrawalRequestStatus.CANCELED);
                    withdrawalRequestRepository.save(withdrawalRequest);
                    res = true;
                }
                case IN_PROGRESS -> {
                    // Option 1: Immediate Cancellation
                     log.warn("Cannot cancel a pending withdrawal immediately.");
                     throw new IllegalStateException("In Progress withdrawals cannot be canceled immediately.");

                    // Option 2: Delayed Cancellation
                    // Mark the withdrawal request as canceled but only reverse the balance deduction if the status changes to 'completed' later.
                    // withdrawalRequest.setStatus(WithdrawalStatus.CANCELED);
                    //withdrawalRequestRepository.save(withdrawalRequest);
                    //res = true; // Marked as canceled
                }
                case CANCELED -> {
                    log.warn("Withdrawal request is already canceled.");
                }
            }
        } else {
            log.error("Withdrawal request with ID {} not found.", withdrawalRequestId);
            throw new WithdrawalRequestNotFoundException("Withdrawal request not found.");
        }
        return res;
    }


    /**
     * Get Bank Account by Card info
     * @param cardNumber
     * @return BankAccount instance
     */
    private BankAccount getBankAccount(String cardNumber) {
        // Initialize a Card object with the card number
        Card card = new Card();
        card.setCardNumber(cardNumber);

        // Use getAccountFromCache to retrieve the BankAccount associated with the card
        return bankAccountService.getAccountFromCacheByCardNumber(card);
    }
}

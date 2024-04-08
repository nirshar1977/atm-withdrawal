package com.bankhapoalim.atmwithdrawal.service;

import com.bankhapoalim.atmwithdrawal.dto.WithdrawalRequestDTO;
import com.bankhapoalim.atmwithdrawal.entity.BankAccount;
import com.bankhapoalim.atmwithdrawal.entity.Card;
import com.bankhapoalim.atmwithdrawal.entity.WithdrawalRequest;
import com.bankhapoalim.atmwithdrawal.enums.WithdrawalStatus;
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
                .build();

        withdrawalRequestRepository.save(withdrawalRequest);

        log.info("Withdrawal request processed successfully: {}", withdrawalRequest);

        return true;
    }

    /**
     * Update user account balance based on the withdrawal request
     * @param withdrawalRequestDTO
     * @return
     */
    private BankAccount updateAccountBalance(WithdrawalRequestDTO withdrawalRequestDTO) {
        // Calculate new balance
        BankAccount bankAccount = getBankAccount(withdrawalRequestDTO.getCardNumber());
        if(bankAccount==null){
            throw new IllegalArgumentException("Bank Account not found"); //TODO: Add more info to exception message
        }
        BigDecimal updatedBalance = bankAccount.getBalance().subtract(withdrawalRequestDTO.getAmount());

        // Update account balance
        bankAccount.setBalance(updatedBalance);
        accountRepository.save(bankAccount);
        return bankAccount;
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
                    bankAccountService.reverseBalance(withdrawalRequest.getBankAccount().getAccountId(), amountToReverse, "Withdrawal cancellation");
                    withdrawalRequest.setStatus(WithdrawalStatus.CANCELED);
                    withdrawalRequestRepository.save(withdrawalRequest);
                    res = true;
                }
                case PENDING -> {
                    // Option 1: Immediate Cancellation
                     log.warn("Cannot cancel a pending withdrawal immediately.");
                     throw new IllegalStateException("Pending withdrawals cannot be canceled immediately.");

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
        return bankAccountService.getAccountFromCache(card);
    }
}

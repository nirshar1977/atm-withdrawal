package com.bankhapoalim.atmwithdrawal.service;

import com.bankhapoalim.atmwithdrawal.dto.WithdrawalRequestDTO;
import com.bankhapoalim.atmwithdrawal.entity.BankAccount;
import com.bankhapoalim.atmwithdrawal.entity.Card;
import com.bankhapoalim.atmwithdrawal.entity.WithdrawalRequest;
import com.bankhapoalim.atmwithdrawal.enums.WithdrawalStatus;
import com.bankhapoalim.atmwithdrawal.repository.WithdrawalRequestRepository;
import com.bankhapoalim.atmwithdrawal.util.ValidationUtils;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Service
@Slf4j
public class WithdrawalService {

    @Autowired
    private ValidationUtils validationUtils;

    @Autowired
    private WithdrawalRequestRepository withdrawalRequestRepository;

    @Autowired
    private BankAccountService bankAccountService;

    public boolean processWithdrawalRequest(WithdrawalRequestDTO withdrawalRequestDTO) {
        if (!validationUtils.isValidWithdrawalRequest().test(withdrawalRequestDTO)) {
            log.error("Invalid withdrawal request: {}", withdrawalRequestDTO);
            return false;
        }

        WithdrawalRequest withdrawalRequest = new WithdrawalRequest();
        withdrawalRequest.setCardNumber(withdrawalRequestDTO.getCardNumber());
        withdrawalRequest.setSecretCode(withdrawalRequestDTO.getSecretCode());
        withdrawalRequest.setAmount(withdrawalRequestDTO.getAmount());
        withdrawalRequest.setBankAccount(getBankAccount(withdrawalRequestDTO.getCardNumber()));

        withdrawalRequestRepository.save(withdrawalRequest);
        log.info("Withdrawal request processed successfully: {}", withdrawalRequestDTO);
        return true;
    }

    /**
     * Cancel a withdrawal request that was not completed.
     *  Steps to consider:
     *      Audit Logging: Log the cancellation event for auditing purposes. This can include details such as who cancelled the request, timestamp, and the reason for cancellation.
     *      Update Account Balance: If the withdrawal amount has already been deducted from the account balance, you may need to update the account balance to reverse the deduction.
     *      Validation Checks: Perform additional checks before allowing cancellation, such as checking if the withdrawal request is still in a cancellable state, verifying the user's authorization to cancel, etc.
     *
     * @param transactionId The ID of the withdrawal request to cancel.
     * @return true if the cancellation is successful, false otherwise.
     */
    public boolean cancelWithdrawalRequest(Long transactionId) {
        Optional<WithdrawalRequest> optionalTransaction = withdrawalRequestRepository.findById(transactionId);
        if (optionalTransaction.isPresent()) {
            WithdrawalRequest transaction = optionalTransaction.get();
            if (transaction.getStatus() == WithdrawalStatus.PENDING) {
                //TODO: Add additional steps detailed in function documentation
                transaction.setStatus(WithdrawalStatus.CANCELED);
                withdrawalRequestRepository.save(transaction);
                // Return true to indicate successful cancellation
                return true;
            }
        }
        // Return false if cancellation is not possible
        return false;
    }

    private BankAccount getBankAccount(String cardNumber) {
        // Initialize a Card object with the card number
        Card card = new Card();
        card.setCardNumber(cardNumber);

        // Use getAccountFromCache to retrieve the BankAccount associated with the card
        return bankAccountService.getAccountFromCache(card);
    }
}

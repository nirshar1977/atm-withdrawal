package com.bankhapoalim.atmwithdrawal.service;

import com.bankhapoalim.atmwithdrawal.dto.WithdrawalRequestDTO;
import com.bankhapoalim.atmwithdrawal.entity.BankAccount;
import com.bankhapoalim.atmwithdrawal.entity.Card;
import com.bankhapoalim.atmwithdrawal.entity.WithdrawalRequest;
import com.bankhapoalim.atmwithdrawal.repository.WithdrawalRequestRepository;
import com.bankhapoalim.atmwithdrawal.util.ValidationUtils;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

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

    private BankAccount getBankAccount(String cardNumber) {
        // Initialize a Card object with the card number
        Card card = new Card();
        card.setCardNumber(cardNumber);

        // Use getAccountFromCache to retrieve the BankAccount associated with the card
        return bankAccountService.getAccountFromCache(card);
    }
}

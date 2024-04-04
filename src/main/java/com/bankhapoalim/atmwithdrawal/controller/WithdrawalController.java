package com.bankhapoalim.atmwithdrawal.controller;

import com.bankhapoalim.atmwithdrawal.dto.WithdrawalRequestDTO;
import com.bankhapoalim.atmwithdrawal.entity.WithdrawalRequest;
import com.bankhapoalim.atmwithdrawal.repository.WithdrawalRequestRepository;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.Optional;

@RestController
@RequestMapping("/api/withdrawal")
public class WithdrawalController {

    private final WithdrawalRequestRepository withdrawalRequestRepository;

    @Autowired
    public WithdrawalController(WithdrawalRequestRepository withdrawalRequestRepository) {
        this.withdrawalRequestRepository = withdrawalRequestRepository;
    }

    @PostMapping("/request")
    public ResponseEntity<String> processWithdrawalRequest(@Valid @RequestBody WithdrawalRequestDTO withdrawalRequestDTO) {

        // Example: Check if the card number and secret code are valid (simulate validation)
        if (!isValidCardAndSecretCode(withdrawalRequestDTO.getCardNumber(), withdrawalRequestDTO.getSecretCode())) {
            return ResponseEntity.status(HttpStatus.FORBIDDEN).body("Invalid card number or secret code");
        }

        // Example: Check withdrawal limits (simulate validation)
        if (!isWithinWithdrawalLimits(withdrawalRequestDTO.getAmount())) {
            return ResponseEntity.status(HttpStatus.FORBIDDEN).body("Exceeded withdrawal limits");
        }

        // Example: Process the withdrawal request (simulate saving to database)
        WithdrawalRequest withdrawalRequest = new WithdrawalRequest();
        withdrawalRequest.setCardNumber(withdrawalRequestDTO.getCardNumber());
        withdrawalRequest.setSecretCode(withdrawalRequestDTO.getSecretCode());
        withdrawalRequest.setAmount(withdrawalRequestDTO.getAmount());
        withdrawalRequestRepository.save(withdrawalRequest);

        return ResponseEntity.ok("Withdrawal request processed successfully");
    }

    // Simulate card and secret code validation
    private boolean isValidCardAndSecretCode(String cardNumber, String secretCode) {
        // Add your validation logic here
        return true; // Simulated validation always returns true
    }

    // Simulate withdrawal limits validation
    private boolean isWithinWithdrawalLimits(double amount) {
        // Add your validation logic here
        return true; // Simulated validation always returns true
    }
}

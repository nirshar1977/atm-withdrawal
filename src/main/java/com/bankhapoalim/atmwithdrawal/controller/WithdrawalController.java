package com.bankhapoalim.atmwithdrawal.controller;

import com.bankhapoalim.atmwithdrawal.dto.WithdrawalRequestDTO;
import com.bankhapoalim.atmwithdrawal.service.WithdrawalService;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/withdrawal")
public class WithdrawalController {

    private final WithdrawalService withdrawalService;

    @Autowired
    public WithdrawalController(WithdrawalService withdrawalService) {
        this.withdrawalService = withdrawalService;
    }

    /**
     * Process a withdrawal request based on the provided data.
     *
     * @param withdrawalRequestDTO The withdrawal request details including card number, secret code, and amount.
     * @return ResponseEntity containing a success message if the request is processed successfully, or an error message otherwise.
     */
    @PostMapping("/request")
    public ResponseEntity<String> processWithdrawalRequest(@Valid @RequestBody WithdrawalRequestDTO withdrawalRequestDTO) {

        boolean processingResult = withdrawalService.processWithdrawalRequest(withdrawalRequestDTO);
        if (processingResult) {
            return ResponseEntity.status(HttpStatus.OK).body("Withdrawal request processed successfully");
        } else {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("Invalid withdrawal amount"); //TODO: change error msg
        }
    }
}

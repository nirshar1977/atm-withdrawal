package com.bankhapoalim.atmwithdrawal.controller;

import com.bankhapoalim.atmwithdrawal.dto.WithdrawalRequestDTO;
import com.bankhapoalim.atmwithdrawal.service.WithdrawalServiceImpl;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/withdrawal")
public class WithdrawalController {

    private final WithdrawalServiceImpl withdrawalService;

    @Autowired
    public WithdrawalController(WithdrawalServiceImpl withdrawalService) {
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

    /**
     * Cancel a withdrawal request that was not completed.
     *
     * @param withdrawalId The ID of the withdrawal request to cancel.
     * @return ResponseEntity containing a success message if the cancellation is successful, or an error message otherwise.
     */
    @DeleteMapping("/cancel/{withdrawalId}")
    public ResponseEntity<String> cancelWithdrawalRequest(@PathVariable Long withdrawalId) {
        boolean cancellationResult = withdrawalService.cancelWithdrawalRequest(withdrawalId);
        if (cancellationResult) {
            return ResponseEntity.status(HttpStatus.OK).body("Withdrawal request canceled successfully");
        } else {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("Failed to cancel withdrawal request");
        }
    }
}

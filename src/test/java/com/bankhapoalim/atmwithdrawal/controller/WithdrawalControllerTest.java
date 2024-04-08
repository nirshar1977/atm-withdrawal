package com.bankhapoalim.atmwithdrawal.controller;

import com.bankhapoalim.atmwithdrawal.dto.WithdrawalRequestDTO;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;
import org.springframework.test.web.servlet.result.MockMvcResultMatchers;

import java.math.BigDecimal;

@SpringBootTest
@AutoConfigureMockMvc
class WithdrawalControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @AfterEach
    void tearDown() {
    }

    @Test
    public void testProcessWithdrawalRequest() throws Exception {
        // Create an instance of WithdrawalRequestDTO and set properties
        WithdrawalRequestDTO requestDTO = new WithdrawalRequestDTO();
        requestDTO.setCardNumber("1234567890123456");
        requestDTO.setSecretCode("1234");
        requestDTO.setAmount(BigDecimal.valueOf(100.0));

        // Convert the object to JSON string
        String requestJson = new ObjectMapper().writeValueAsString(requestDTO);

        // Perform the POST request with the JSON body
        mockMvc.perform(MockMvcRequestBuilders.post("/api/withdrawal/request")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(requestJson))
                .andExpect(MockMvcResultMatchers.status().isOk())
                .andExpect(MockMvcResultMatchers.content().string("Withdrawal request processed successfully"));
    }

    @Test
    public void testCancelWithdrawalRequest() throws Exception {
        mockMvc.perform(MockMvcRequestBuilders.delete("/api/withdrawal/cancel/1"))
                .andExpect(MockMvcResultMatchers.status().isOk())
                .andExpect(MockMvcResultMatchers.content().string("Withdrawal request canceled successfully"));
    }

    // Helper method to convert objects to JSON string
    private String asJsonString(final Object obj) {
        try {
            return new ObjectMapper().writeValueAsString(obj);
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }
}
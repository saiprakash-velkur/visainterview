package com.example.visainterview.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import lombok.Data;

import java.math.BigDecimal;

@Data
public class CreateTransactionRequest {
    @NotNull(message = "Account ID is required")
    @JsonProperty("accountId")
    private Long accountId;
    
    @NotNull(message = "Transaction type is required")
    @JsonProperty("transactionType")
    private Integer transactionType;
    
    @NotNull(message = "Amount is required")
    @Positive(message = "Amount must be positive")
    @JsonProperty("amount")
    private BigDecimal amount;
    
    @NotNull(message = "Idempotency key is required")
    @JsonProperty("idempotencyKey")
    private String idempotencyKey;
}

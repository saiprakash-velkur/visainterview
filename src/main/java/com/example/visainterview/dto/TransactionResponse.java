package com.example.visainterview.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class TransactionResponse {
    @JsonProperty("transactionId")
    private Long transactionId;
    
    @JsonProperty("accountId")
    private Long accountId;
    
    @JsonProperty("transactionType")
    private Integer transactionType;
    
    @JsonProperty("amount")
    private BigDecimal amount;
    
    @JsonProperty("transactionDate")
    private LocalDateTime transactionDate;
}

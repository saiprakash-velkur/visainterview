package com.example.visainterview.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class AccountResponse {
    @JsonProperty("accountId")
    private Long accountId;
    
    @JsonProperty("documentNumber")
    private String documentNumber;
    
    @JsonProperty("balance")
    private BigDecimal balance;
}

package com.example.visainterview.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import jakarta.validation.constraints.NotBlank;
import lombok.Data;

@Data
public class CreateAccountRequest {
    @NotBlank(message = "Document number is required")
    @JsonProperty("documentNumber")
    private String documentNumber;
}

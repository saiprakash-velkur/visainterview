package com.example.visainterview.controller;

import com.example.visainterview.dto.CreateTransactionRequest;
import com.example.visainterview.dto.TransactionResponse;
import com.example.visainterview.exception.AccountNotFoundException;
import com.example.visainterview.exception.InsufficientBalanceException;
import com.example.visainterview.exception.InvalidTransactionTypeException;
import com.example.visainterview.service.TransactionService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.math.BigDecimal;
import java.time.LocalDateTime;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class TransactionControllerTest {

    @Mock
    private TransactionService transactionService;

    @InjectMocks
    private TransactionController transactionController;

    private CreateTransactionRequest request;
    private TransactionResponse response;

    @BeforeEach
    void setUp() {
        request = new CreateTransactionRequest();
        request.setAccountId(1L);
        request.setTransactionType(4);
        request.setAmount(new BigDecimal("100.00"));
        request.setIdempotencyKey("test-key-123");

        response = TransactionResponse.builder()
                .transactionId(1L)
                .accountId(1L)
                .transactionType(4)
                .amount(new BigDecimal("100.00"))
                .transactionDate(LocalDateTime.now())
                .build();
    }

    @Test
    void createTransaction_Success() {
        when(transactionService.createTransaction(any(CreateTransactionRequest.class))).thenReturn(response);

        TransactionResponse result = transactionController.createTransaction(request);

        assertNotNull(result);
        assertEquals(1L, result.getTransactionId());
        assertEquals(1L, result.getAccountId());
        assertEquals(4, result.getTransactionType());
        assertEquals(new BigDecimal("100.00"), result.getAmount());

        verify(transactionService).createTransaction(any(CreateTransactionRequest.class));
    }

    @Test
    void createTransaction_AccountNotFound_ThrowsException() {
        when(transactionService.createTransaction(any(CreateTransactionRequest.class)))
                .thenThrow(new AccountNotFoundException("Account not found"));

        assertThrows(AccountNotFoundException.class, () -> {
            transactionController.createTransaction(request);
        });

        verify(transactionService).createTransaction(any(CreateTransactionRequest.class));
    }

    @Test
    void createTransaction_InsufficientBalance_ThrowsException() {
        when(transactionService.createTransaction(any(CreateTransactionRequest.class)))
                .thenThrow(new InsufficientBalanceException("Insufficient balance"));

        assertThrows(InsufficientBalanceException.class, () -> {
            transactionController.createTransaction(request);
        });

        verify(transactionService).createTransaction(any(CreateTransactionRequest.class));
    }

    @Test
    void createTransaction_InvalidTransactionType_ThrowsException() {
        when(transactionService.createTransaction(any(CreateTransactionRequest.class)))
                .thenThrow(new InvalidTransactionTypeException("Invalid transaction type: 99"));

        assertThrows(InvalidTransactionTypeException.class, () -> {
            transactionController.createTransaction(request);
        });

        verify(transactionService).createTransaction(any(CreateTransactionRequest.class));
    }
}

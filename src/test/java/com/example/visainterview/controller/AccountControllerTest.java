package com.example.visainterview.controller;

import com.example.visainterview.dto.AccountResponse;
import com.example.visainterview.dto.CreateAccountRequest;
import com.example.visainterview.exception.AccountNotFoundException;
import com.example.visainterview.exception.DuplicateDocumentNumberException;
import com.example.visainterview.service.AccountService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

import java.math.BigDecimal;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class AccountControllerTest {

    @Mock
    private AccountService accountService;

    @InjectMocks
    private AccountController accountController;

    private CreateAccountRequest createAccountRequest;
    private AccountResponse accountResponse;

    @BeforeEach
    void setUp() {
        createAccountRequest = new CreateAccountRequest();
        createAccountRequest.setDocumentNumber("12345678900");

        accountResponse = AccountResponse.builder()
                .accountId(1L)
                .documentNumber("12345678900")
                .balance(BigDecimal.ZERO)
                .build();
    }

    @Test
    void createAccount_Success() {
        when(accountService.createAccount(any(CreateAccountRequest.class))).thenReturn(accountResponse);

        AccountResponse response = accountController.createAccount(createAccountRequest);

        assertNotNull(response);
        assertEquals(1L, response.getAccountId());
        assertEquals("12345678900", response.getDocumentNumber());
        assertEquals(BigDecimal.ZERO, response.getBalance());

        verify(accountService).createAccount(any(CreateAccountRequest.class));
    }

    @Test
    void createAccount_DuplicateDocumentNumber_ThrowsException() {
        when(accountService.createAccount(any(CreateAccountRequest.class)))
                .thenThrow(new DuplicateDocumentNumberException("Document number already exists"));

        assertThrows(DuplicateDocumentNumberException.class, () -> {
            accountController.createAccount(createAccountRequest);
        });

        verify(accountService).createAccount(any(CreateAccountRequest.class));
    }

    @Test
    void getAccount_Success() {
        when(accountService.getAccount(1L)).thenReturn(accountResponse);

        AccountResponse response = accountController.getAccount(1L);

        assertNotNull(response);
        assertEquals(1L, response.getAccountId());
        assertEquals("12345678900", response.getDocumentNumber());
        assertEquals(BigDecimal.ZERO, response.getBalance());

        verify(accountService).getAccount(1L);
    }

    @Test
    void getAccount_NotFound_ThrowsException() {
        when(accountService.getAccount(1L))
                .thenThrow(new AccountNotFoundException("Account not found"));

        assertThrows(AccountNotFoundException.class, () -> {
            accountController.getAccount(1L);
        });

        verify(accountService).getAccount(1L);
    }
}

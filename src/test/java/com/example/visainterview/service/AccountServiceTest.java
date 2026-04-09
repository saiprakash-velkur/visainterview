package com.example.visainterview.service;

import com.example.visainterview.dto.AccountResponse;
import com.example.visainterview.dto.CreateAccountRequest;
import com.example.visainterview.entity.Account;
import com.example.visainterview.entity.Customer;
import com.example.visainterview.exception.AccountNotFoundException;
import com.example.visainterview.exception.DuplicateDocumentNumberException;
import com.example.visainterview.repository.AccountRepository;
import com.example.visainterview.repository.CustomerRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.math.BigDecimal;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class AccountServiceTest {

    @Mock
    private AccountRepository accountRepository;

    @Mock
    private CustomerRepository customerRepository;

    @InjectMocks
    private AccountService accountService;

    private CreateAccountRequest createAccountRequest;
    private Customer customer;
    private Account account;

    @BeforeEach
    void setUp() {
        createAccountRequest = new CreateAccountRequest();
        createAccountRequest.setDocumentNumber("12345678900");

        customer = Customer.builder()
                .id(1L)
                .documentNumber("12345678900")
                .build();

        account = Account.builder()
                .id(1L)
                .customer(customer)
                .balance(BigDecimal.ZERO)
                .build();
    }

    @Test
    void createAccount_Success() {
        when(customerRepository.existsByDocumentNumber(anyString())).thenReturn(false);
        when(customerRepository.save(any(Customer.class))).thenReturn(customer);
        when(accountRepository.save(any(Account.class))).thenReturn(account);

        AccountResponse response = accountService.createAccount(createAccountRequest);

        assertNotNull(response);
        assertEquals(1L, response.getAccountId());
        assertEquals("12345678900", response.getDocumentNumber());
        assertEquals(BigDecimal.ZERO, response.getBalance());

        verify(customerRepository).existsByDocumentNumber("12345678900");
        verify(customerRepository).save(any(Customer.class));
        verify(accountRepository).save(any(Account.class));
    }

    @Test
    void createAccount_DuplicateDocumentNumber_ThrowsException() {
        when(customerRepository.existsByDocumentNumber(anyString())).thenReturn(true);

        assertThrows(DuplicateDocumentNumberException.class, () -> {
            accountService.createAccount(createAccountRequest);
        });

        verify(customerRepository).existsByDocumentNumber("12345678900");
        verify(customerRepository, never()).save(any(Customer.class));
        verify(accountRepository, never()).save(any(Account.class));
    }

    @Test
    void getAccount_Success() {
        when(accountRepository.findById(1L)).thenReturn(Optional.of(account));

        AccountResponse response = accountService.getAccount(1L);

        assertNotNull(response);
        assertEquals(1L, response.getAccountId());
        assertEquals("12345678900", response.getDocumentNumber());
        assertEquals(BigDecimal.ZERO, response.getBalance());

        verify(accountRepository).findById(1L);
    }

    @Test
    void getAccount_NotFound_ThrowsException() {
        when(accountRepository.findById(1L)).thenReturn(Optional.empty());

        assertThrows(AccountNotFoundException.class, () -> {
            accountService.getAccount(1L);
        });

        verify(accountRepository).findById(1L);
    }

    @Test
    void createAccount_SetsDefaultBalance() {
        when(customerRepository.existsByDocumentNumber(anyString())).thenReturn(false);
        when(customerRepository.save(any(Customer.class))).thenReturn(customer);
        when(accountRepository.save(any(Account.class))).thenReturn(account);

        AccountResponse response = accountService.createAccount(createAccountRequest);

        assertEquals(BigDecimal.ZERO, response.getBalance());
    }
}

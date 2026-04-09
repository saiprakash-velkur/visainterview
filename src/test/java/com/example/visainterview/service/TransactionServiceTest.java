package com.example.visainterview.service;

import com.example.visainterview.dto.CreateTransactionRequest;
import com.example.visainterview.dto.TransactionResponse;
import com.example.visainterview.entity.Account;
import com.example.visainterview.entity.Customer;
import com.example.visainterview.entity.Transaction;
import com.example.visainterview.entity.TransactionType;
import com.example.visainterview.exception.AccountNotFoundException;
import com.example.visainterview.exception.InsufficientBalanceException;
import com.example.visainterview.exception.InvalidTransactionTypeException;
import com.example.visainterview.repository.AccountRepository;
import com.example.visainterview.repository.TransactionRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class TransactionServiceTest {

    @Mock
    private TransactionRepository transactionRepository;

    @Mock
    private AccountRepository accountRepository;

    @InjectMocks
    private TransactionService transactionService;

    private CreateTransactionRequest request;
    private Account account;
    private Customer customer;
    private Transaction transaction;

    @BeforeEach
    void setUp() {
        customer = Customer.builder()
                .id(1L)
                .documentNumber("12345678900")
                .build();

        account = Account.builder()
                .id(1L)
                .customer(customer)
                .balance(new BigDecimal("500.00"))
                .build();

        request = new CreateTransactionRequest();
        request.setAccountId(1L);
        request.setTransactionType(4);
        request.setAmount(new BigDecimal("100.00"));
        request.setIdempotencyKey("test-key-123");

        transaction = Transaction.builder()
                .id(1L)
                .account(account)
                .transactionType(TransactionType.CREDIT_VOUCHER)
                .amount(new BigDecimal("100.00"))
                .idempotencyKey("test-key-123")
                .build();
    }

    @Test
    void createTransaction_CreditVoucher_Success() {
        when(transactionRepository.findByIdempotencyKey(anyString())).thenReturn(Optional.empty());
        when(accountRepository.findById(1L)).thenReturn(Optional.of(account));
        when(accountRepository.save(any(Account.class))).thenReturn(account);
        when(transactionRepository.save(any(Transaction.class))).thenReturn(transaction);

        TransactionResponse response = transactionService.createTransaction(request);

        assertNotNull(response);
        assertEquals(1L, response.getTransactionId());
        assertEquals(1L, response.getAccountId());
        assertEquals(4, response.getTransactionType());
        assertEquals(new BigDecimal("100.00"), response.getAmount());

        verify(accountRepository).save(any(Account.class));
        verify(transactionRepository).save(any(Transaction.class));
    }

    @Test
    void createTransaction_NormalPurchase_Success() {
        request.setTransactionType(1);
        request.setAmount(new BigDecimal("100.00"));

        when(transactionRepository.findByIdempotencyKey(anyString())).thenReturn(Optional.empty());
        when(accountRepository.findById(1L)).thenReturn(Optional.of(account));
        when(accountRepository.save(any(Account.class))).thenReturn(account);
        when(transactionRepository.save(any(Transaction.class))).thenReturn(transaction);

        TransactionResponse response = transactionService.createTransaction(request);

        assertNotNull(response);
        assertEquals(new BigDecimal("400.00"), account.getBalance());

        verify(accountRepository).save(any(Account.class));
        verify(transactionRepository).save(any(Transaction.class));
    }

    @Test
    void createTransaction_Withdrawal_Success() {
        request.setTransactionType(3);
        request.setAmount(new BigDecimal("50.00"));

        when(transactionRepository.findByIdempotencyKey(anyString())).thenReturn(Optional.empty());
        when(accountRepository.findById(1L)).thenReturn(Optional.of(account));
        when(accountRepository.save(any(Account.class))).thenReturn(account);
        when(transactionRepository.save(any(Transaction.class))).thenReturn(transaction);

        TransactionResponse response = transactionService.createTransaction(request);

        assertNotNull(response);
        assertEquals(new BigDecimal("450.00"), account.getBalance());

        verify(accountRepository).save(any(Account.class));
        verify(transactionRepository).save(any(Transaction.class));
    }

    @Test
    void createTransaction_InsufficientBalance_ThrowsException() {
        request.setTransactionType(1);
        request.setAmount(new BigDecimal("1000.00"));

        when(transactionRepository.findByIdempotencyKey(anyString())).thenReturn(Optional.empty());
        when(accountRepository.findById(1L)).thenReturn(Optional.of(account));

        assertThrows(InsufficientBalanceException.class, () -> {
            transactionService.createTransaction(request);
        });

        verify(accountRepository, never()).save(any(Account.class));
        verify(transactionRepository, never()).save(any(Transaction.class));
    }

    @Test
    void createTransaction_AccountNotFound_ThrowsException() {
        when(transactionRepository.findByIdempotencyKey(anyString())).thenReturn(Optional.empty());
        when(accountRepository.findById(1L)).thenReturn(Optional.empty());

        assertThrows(AccountNotFoundException.class, () -> {
            transactionService.createTransaction(request);
        });

        verify(accountRepository, never()).save(any(Account.class));
        verify(transactionRepository, never()).save(any(Transaction.class));
    }

    @Test
    void createTransaction_InvalidTransactionType_ThrowsException() {
        request.setTransactionType(99);

        when(transactionRepository.findByIdempotencyKey(anyString())).thenReturn(Optional.empty());
        when(accountRepository.findById(1L)).thenReturn(Optional.of(account));

        assertThrows(InvalidTransactionTypeException.class, () -> {
            transactionService.createTransaction(request);
        });

        verify(accountRepository, never()).save(any(Account.class));
        verify(transactionRepository, never()).save(any(Transaction.class));
    }

    @Test
    void createTransaction_IdempotencyCheck_ReturnsExistingTransaction() {
        when(transactionRepository.findByIdempotencyKey("test-key-123")).thenReturn(Optional.of(transaction));

        TransactionResponse response = transactionService.createTransaction(request);

        assertNotNull(response);
        assertEquals(1L, response.getTransactionId());

        verify(accountRepository, never()).findById(any());
        verify(accountRepository, never()).save(any(Account.class));
        verify(transactionRepository, never()).save(any(Transaction.class));
    }

    @Test
    void createTransaction_CreditIncreasesBalance() {
        BigDecimal initialBalance = account.getBalance();
        request.setTransactionType(4);
        request.setAmount(new BigDecimal("100.00"));

        when(transactionRepository.findByIdempotencyKey(anyString())).thenReturn(Optional.empty());
        when(accountRepository.findById(1L)).thenReturn(Optional.of(account));
        when(accountRepository.save(any(Account.class))).thenReturn(account);
        when(transactionRepository.save(any(Transaction.class))).thenReturn(transaction);

        transactionService.createTransaction(request);

        assertEquals(initialBalance.add(new BigDecimal("100.00")), account.getBalance());
    }

    @Test
    void createTransaction_DebitDecreasesBalance() {
        BigDecimal initialBalance = account.getBalance();
        request.setTransactionType(1);
        request.setAmount(new BigDecimal("100.00"));

        when(transactionRepository.findByIdempotencyKey(anyString())).thenReturn(Optional.empty());
        when(accountRepository.findById(1L)).thenReturn(Optional.of(account));
        when(accountRepository.save(any(Account.class))).thenReturn(account);
        when(transactionRepository.save(any(Transaction.class))).thenReturn(transaction);

        transactionService.createTransaction(request);

        assertEquals(initialBalance.subtract(new BigDecimal("100.00")), account.getBalance());
    }
}

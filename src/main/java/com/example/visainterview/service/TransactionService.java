package com.example.visainterview.service;

import com.example.visainterview.dto.CreateTransactionRequest;
import com.example.visainterview.dto.TransactionResponse;
import com.example.visainterview.entity.Account;
import com.example.visainterview.entity.Transaction;
import com.example.visainterview.entity.TransactionType;
import com.example.visainterview.exception.AccountNotFoundException;
import com.example.visainterview.exception.InsufficientBalanceException;
import com.example.visainterview.exception.InvalidTransactionTypeException;
import com.example.visainterview.repository.AccountRepository;
import com.example.visainterview.repository.TransactionRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class TransactionService {

    private final TransactionRepository transactionRepository;
    private final AccountRepository accountRepository;

    @Transactional
    public TransactionResponse createTransaction(CreateTransactionRequest request) {
        Optional<Transaction> existingTransaction = transactionRepository
                .findByIdempotencyKey(request.getIdempotencyKey());
        if (existingTransaction.isPresent()) {
            return mapToResponse(existingTransaction.get());
        }

        TransactionType transactionType;
        try {
            transactionType = TransactionType.fromCode(request.getTransactionType());
        } catch (IllegalArgumentException e) {
            throw new InvalidTransactionTypeException("Invalid transaction type: " + request.getTransactionType());
        }

        Account account = accountRepository.findById(request.getAccountId())
                .orElseThrow(() -> new AccountNotFoundException("Account not found"));

        if (transactionType.isDebit()) {
            if (account.getBalance().compareTo(request.getAmount()) < 0) {
                throw new InsufficientBalanceException("Insufficient balance");
            }
            account.setBalance(account.getBalance().subtract(request.getAmount()));
        } else if (transactionType.isCredit()) {
            account.setBalance(account.getBalance().add(request.getAmount()));
        }

        accountRepository.save(account);

        Transaction transaction = Transaction.builder()
                .account(account)
                .transactionType(transactionType)
                .amount(request.getAmount())
                .idempotencyKey(request.getIdempotencyKey())
                .build();
        transaction = transactionRepository.save(transaction);

        return mapToResponse(transaction);
    }

    private TransactionResponse mapToResponse(Transaction transaction) {
        return TransactionResponse.builder()
                .transactionId(transaction.getId())
                .accountId(transaction.getAccount().getId())
                .transactionType(transaction.getTransactionType().getCode())
                .amount(transaction.getAmount())
                .transactionDate(transaction.getCreatedAt())
                .build();
    }
}

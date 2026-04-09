package com.example.visainterview.service;

import com.example.visainterview.dto.AccountResponse;
import com.example.visainterview.dto.CreateAccountRequest;
import com.example.visainterview.entity.Account;
import com.example.visainterview.entity.Customer;
import com.example.visainterview.exception.AccountNotFoundException;
import com.example.visainterview.exception.DuplicateDocumentNumberException;
import com.example.visainterview.repository.AccountRepository;
import com.example.visainterview.repository.CustomerRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;

@Service
@RequiredArgsConstructor
public class AccountService {

        private final AccountRepository accountRepository;
        private final CustomerRepository customerRepository;

        @Transactional
        public AccountResponse createAccount(CreateAccountRequest request) {
                if (customerRepository.existsByDocumentNumber(request.getDocumentNumber())) {
                        throw new DuplicateDocumentNumberException("Document number already exists");
                }

                Customer customer = Customer.builder()
                                .documentNumber(request.getDocumentNumber())
                                .build();
                customer = customerRepository.save(customer);

                Account account = Account.builder()
                                .customer(customer)
                                .balance(BigDecimal.ZERO)
                                .build();
                account = accountRepository.save(account);

                return AccountResponse.builder()
                                .accountId(account.getId())
                                .documentNumber(customer.getDocumentNumber())
                                .balance(account.getBalance())
                                .build();
        }

        @Transactional(readOnly = true)
        public AccountResponse getAccount(Long accountId) {
                Account account = accountRepository.findById(accountId)
                                .orElseThrow(() -> new AccountNotFoundException("Account not found"));

                return AccountResponse.builder()
                                .accountId(account.getId())
                                .balance(account.getBalance())
                                .documentNumber(account.getCustomer().getDocumentNumber())
                                .build();
        }
}

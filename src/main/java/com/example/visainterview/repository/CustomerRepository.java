package com.example.visainterview.repository;

import com.example.visainterview.entity.Customer;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface CustomerRepository extends JpaRepository<Customer, Long> {
    Optional<Customer> findByDocumentNumber(String documentNumber);
    boolean existsByDocumentNumber(String documentNumber);
}

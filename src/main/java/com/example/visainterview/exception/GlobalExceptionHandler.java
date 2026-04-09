package com.example.visainterview.exception;

import com.example.visainterview.dto.ErrorResponse;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.http.HttpStatus;
import org.springframework.orm.ObjectOptimisticLockingFailureException;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestControllerAdvice;

@RestControllerAdvice
public class GlobalExceptionHandler {
    
    @ExceptionHandler(DuplicateDocumentNumberException.class)
    @ResponseStatus(HttpStatus.CONFLICT)
    public ErrorResponse handleDuplicateDocumentNumber(DuplicateDocumentNumberException ex) {
        return ErrorResponse.builder()
                .errorCode("DUPLICATE_DOCUMENT_NUMBER")
                .message(ex.getMessage())
                .build();
    }
    
    @ExceptionHandler(AccountNotFoundException.class)
    @ResponseStatus(HttpStatus.NOT_FOUND)
    public ErrorResponse handleAccountNotFound(AccountNotFoundException ex) {
        return ErrorResponse.builder()
                .errorCode("ACCOUNT_NOT_FOUND")
                .message(ex.getMessage())
                .build();
    }
    
    @ExceptionHandler(InsufficientBalanceException.class)
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    public ErrorResponse handleInsufficientBalance(InsufficientBalanceException ex) {
        return ErrorResponse.builder()
                .errorCode("INSUFFICIENT_BALANCE")
                .message(ex.getMessage())
                .build();
    }
    
    @ExceptionHandler(InvalidTransactionTypeException.class)
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    public ErrorResponse handleInvalidTransactionType(InvalidTransactionTypeException ex) {
        return ErrorResponse.builder()
                .errorCode("INVALID_TRANSACTION_TYPE")
                .message(ex.getMessage())
                .build();
    }
    
    @ExceptionHandler(MethodArgumentNotValidException.class)
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    public ErrorResponse handleValidationErrors(MethodArgumentNotValidException ex) {
        FieldError fieldError = ex.getBindingResult().getFieldError();
        String message = fieldError != null ? fieldError.getDefaultMessage() : "Validation failed";
        return ErrorResponse.builder()
                .errorCode("VALIDATION_ERROR")
                .message(message)
                .build();
    }
    
    @ExceptionHandler(ObjectOptimisticLockingFailureException.class)
    @ResponseStatus(HttpStatus.CONFLICT)
    public ErrorResponse handleOptimisticLockingFailure(ObjectOptimisticLockingFailureException ex) {
        return ErrorResponse.builder()
                .errorCode("CONCURRENT_UPDATE")
                .message("Transaction failed due to concurrent update. Please retry.")
                .build();
    }
    
    @ExceptionHandler(DataIntegrityViolationException.class)
    @ResponseStatus(HttpStatus.CONFLICT)
    public ErrorResponse handleDataIntegrityViolation(DataIntegrityViolationException ex) {
        return ErrorResponse.builder()
                .errorCode("DATA_INTEGRITY_VIOLATION")
                .message("Data integrity constraint violated")
                .build();
    }
}

package utils

import "errors"

var (
	ErrDocumentNumberExists = errors.New("document number already exists")
	ErrDocumentNumberRequired = errors.New("document number is required")
	ErrAccountNotFound = errors.New("account not found")
	ErrInvalidTransactionType = errors.New("invalid transaction type")
	ErrInsufficientBalance = errors.New("insufficient balance")
	ErrIdempotencyKeyExists = errors.New("idempotency key already exists")
	ErrOptimisticLockFailed = errors.New("optimistic lock failed, please retry")
)

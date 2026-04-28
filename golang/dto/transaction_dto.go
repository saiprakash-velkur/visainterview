package dto

import "time"

type CreateTransactionRequest struct {
	AccountID       uint    `json:"accountId" binding:"required"`
	TransactionType int     `json:"transactionType" binding:"required"`
	Amount          float64 `json:"amount" binding:"required,gt=0"`
	IdempotencyKey  string  `json:"idempotencyKey" binding:"required"`
}

type TransactionResponse struct {
	TransactionID   uint      `json:"transactionId"`
	AccountID       uint      `json:"accountId"`
	TransactionType int       `json:"transactionType"`
	Amount          float64   `json:"amount"`
	TransactionDate time.Time `json:"transactionDate"`
}

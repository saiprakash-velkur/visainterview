package service

import (
	"context"
	"errors"

	"github.com/visainterview/financial-transaction-system/dto"
	"github.com/visainterview/financial-transaction-system/middleware"
	"github.com/visainterview/financial-transaction-system/models"
	"github.com/visainterview/financial-transaction-system/repository"
	"github.com/visainterview/financial-transaction-system/utils"
	"gorm.io/gorm"
)

type TransactionService interface {
	CreateTransaction(ctx context.Context, req dto.CreateTransactionRequest) (*dto.TransactionResponse, error)
}

type transactionService struct {
	transactionRepo repository.TransactionRepository
	accountRepo     repository.AccountRepository
}

func NewTransactionService(transactionRepo repository.TransactionRepository, accountRepo repository.AccountRepository) TransactionService {
	return &transactionService{
		transactionRepo: transactionRepo,
		accountRepo:     accountRepo,
	}
}

func (s *transactionService) CreateTransaction(ctx context.Context, req dto.CreateTransactionRequest) (*dto.TransactionResponse, error) {
	transactionType := models.TransactionType(req.TransactionType)
	if !transactionType.IsValid() {
		return nil, utils.ErrInvalidTransactionType
	}

	existingTxn, err := s.transactionRepo.FindByIdempotencyKey(ctx, req.IdempotencyKey)
	if err == nil && existingTxn != nil {
		return &dto.TransactionResponse{
			TransactionID:   existingTxn.ID,
			AccountID:       existingTxn.AccountID,
			TransactionType: int(existingTxn.TransactionType),
			Amount:          existingTxn.Amount,
			TransactionDate: existingTxn.CreatedAt,
		}, utils.ErrIdempotencyKeyExists
	}

	var transaction *models.Transaction
	const maxRetries = 2

	for attempt := 0; attempt <= maxRetries; attempt++ {
		account, err := s.accountRepo.FindByID(ctx, req.AccountID)
		if err != nil {
			if errors.Is(err, gorm.ErrRecordNotFound) {
				return nil, utils.ErrAccountNotFound
			}
			return nil, err
		}

		newBalance := account.Balance
		if transactionType.IsDebit() {
			if account.Balance < req.Amount {
				return nil, utils.ErrInsufficientBalance
			}
			newBalance -= req.Amount
		} else if transactionType.IsCredit() {
			newBalance += req.Amount
		}

		if transaction == nil {
			transaction = &models.Transaction{
				AccountID:       req.AccountID,
				TransactionType: transactionType,
				Amount:          req.Amount,
				IdempotencyKey:  req.IdempotencyKey,
			}

			if err := s.transactionRepo.Create(ctx, transaction); err != nil {
				return nil, err
			}
		}

		err = s.accountRepo.UpdateBalance(ctx, account, newBalance)
		if err == nil {
			txTypeStr := transactionType.String()
			middleware.RecordTransaction(txTypeStr, "success", req.Amount)

			return &dto.TransactionResponse{
				TransactionID:   transaction.ID,
				AccountID:       transaction.AccountID,
				TransactionType: int(transaction.TransactionType),
				Amount:          transaction.Amount,
				TransactionDate: transaction.CreatedAt,
			}, nil
		}

		if !errors.Is(err, gorm.ErrRecordNotFound) {
			return nil, err
		}

		middleware.RecordOptimisticLockRetry()

		if attempt == maxRetries {
			return nil, utils.ErrOptimisticLockFailed
		}
	}

	return nil, utils.ErrOptimisticLockFailed
}

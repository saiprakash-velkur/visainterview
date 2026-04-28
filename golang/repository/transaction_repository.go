package repository

import (
	"context"

	"github.com/visainterview/financial-transaction-system/models"
	"gorm.io/gorm"
)

type TransactionRepository interface {
	Create(ctx context.Context, transaction *models.Transaction) error
	FindByIdempotencyKey(ctx context.Context, key string) (*models.Transaction, error)
}

type transactionRepository struct {
	db *gorm.DB
}

func NewTransactionRepository(db *gorm.DB) TransactionRepository {
	return &transactionRepository{db: db}
}

func (r *transactionRepository) Create(ctx context.Context, transaction *models.Transaction) error {
	return r.db.WithContext(ctx).Create(transaction).Error
}

func (r *transactionRepository) FindByIdempotencyKey(ctx context.Context, key string) (*models.Transaction, error) {
	var transaction models.Transaction
	err := r.db.WithContext(ctx).Where("idempotency_key = ?", key).First(&transaction).Error
	return &transaction, err
}

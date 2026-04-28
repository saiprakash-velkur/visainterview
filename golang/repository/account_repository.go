package repository

import (
	"context"

	"github.com/visainterview/financial-transaction-system/models"
	"gorm.io/gorm"
)

type AccountRepository interface {
	CreateCustomerAndAccount(ctx context.Context, customer *models.Customer, account *models.Account) error
	FindByID(ctx context.Context, id uint) (*models.Account, error)
	FindByDocumentNumber(ctx context.Context, documentNumber string) (*models.Customer, error)
	UpdateBalance(ctx context.Context, account *models.Account, newBalance float64) error
}

type accountRepository struct {
	db *gorm.DB
}

func NewAccountRepository(db *gorm.DB) AccountRepository {
	return &accountRepository{db: db}
}

func (r *accountRepository) CreateCustomerAndAccount(ctx context.Context, customer *models.Customer, account *models.Account) error {
	return r.db.WithContext(ctx).Transaction(func(tx *gorm.DB) error {
		if err := tx.Create(customer).Error; err != nil {
			return err
		}
		account.CustomerID = customer.ID
		return tx.Create(account).Error
	})
}

func (r *accountRepository) FindByID(ctx context.Context, id uint) (*models.Account, error) {
	var account models.Account
	err := r.db.WithContext(ctx).Preload("Customer").First(&account, id).Error
	return &account, err
}

func (r *accountRepository) FindByDocumentNumber(ctx context.Context, documentNumber string) (*models.Customer, error) {
	var customer models.Customer
	err := r.db.WithContext(ctx).Where("document_number = ?", documentNumber).First(&customer).Error
	return &customer, err
}

func (r *accountRepository) UpdateBalance(ctx context.Context, account *models.Account, newBalance float64) error {
	result := r.db.WithContext(ctx).Model(account).
		Where("id = ? AND version = ?", account.ID, account.Version).
		Updates(map[string]interface{}{
			"balance": newBalance,
			"version": account.Version + 1,
		})
	
	if result.Error != nil {
		return result.Error
	}
	
	if result.RowsAffected == 0 {
		return gorm.ErrRecordNotFound
	}
	
	return nil
}

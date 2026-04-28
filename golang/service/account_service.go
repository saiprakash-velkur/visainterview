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

type AccountService interface {
	CreateAccount(ctx context.Context, req dto.CreateAccountRequest) (*dto.AccountResponse, error)
	GetAccount(ctx context.Context, accountID uint) (*dto.AccountResponse, error)
}

type accountService struct {
	accountRepo repository.AccountRepository
}

func NewAccountService(accountRepo repository.AccountRepository) AccountService {
	return &accountService{accountRepo: accountRepo}
}

func (s *accountService) CreateAccount(ctx context.Context, req dto.CreateAccountRequest) (*dto.AccountResponse, error) {
	if req.DocumentNumber == "" {
		return nil, utils.ErrDocumentNumberRequired
	}

	existing, err := s.accountRepo.FindByDocumentNumber(ctx, req.DocumentNumber)
	if err == nil && existing != nil {
		return nil, utils.ErrDocumentNumberExists
	}

	customer := &models.Customer{
		DocumentNumber: req.DocumentNumber,
	}

	account := &models.Account{
		Balance: 0,
		Version: 0,
	}

	if err := s.accountRepo.CreateCustomerAndAccount(ctx, customer, account); err != nil {
		return nil, err
	}

	middleware.RecordAccountCreation()

	return &dto.AccountResponse{
		AccountID:      account.ID,
		DocumentNumber: customer.DocumentNumber,
		Balance:        account.Balance,
	}, nil
}

func (s *accountService) GetAccount(ctx context.Context, accountID uint) (*dto.AccountResponse, error) {
	account, err := s.accountRepo.FindByID(ctx, accountID)
	if err != nil {
		if errors.Is(err, gorm.ErrRecordNotFound) {
			return nil, utils.ErrAccountNotFound
		}
		return nil, err
	}

	return &dto.AccountResponse{
		AccountID:      account.ID,
		DocumentNumber: account.Customer.DocumentNumber,
		Balance:        account.Balance,
	}, nil
}

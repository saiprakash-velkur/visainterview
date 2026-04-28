package models

import "time"

type Transaction struct {
	ID             uint            `gorm:"primaryKey"`
	AccountID      uint            `gorm:"not null;index"`
	Account        Account         `gorm:"foreignKey:AccountID"`
	TransactionType TransactionType `gorm:"not null"`
	Amount         float64         `gorm:"type:decimal(15,2);not null"`
	IdempotencyKey string          `gorm:"uniqueIndex;not null"`
	CreatedAt      time.Time
	UpdatedAt      time.Time
}

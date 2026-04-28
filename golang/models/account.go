package models

import "time"

type Account struct {
	ID         uint      `gorm:"primaryKey"`
	CustomerID uint      `gorm:"not null;uniqueIndex"`
	Customer   Customer  `gorm:"foreignKey:CustomerID"`
	Balance    float64   `gorm:"type:decimal(15,2);default:0;not null"`
	Version    int       `gorm:"not null;default:0"`
	CreatedAt  time.Time
	UpdatedAt  time.Time
}

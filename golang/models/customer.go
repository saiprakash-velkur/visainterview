package models

import "time"

type Customer struct {
	ID             uint      `gorm:"primaryKey"`
	DocumentNumber string    `gorm:"uniqueIndex;not null"`
	Name           string    `gorm:"type:varchar(255)"`
	CreatedAt      time.Time
	UpdatedAt      time.Time
}

package dto

type CreateAccountRequest struct {
	DocumentNumber string `json:"documentNumber" binding:"required"`
}

type AccountResponse struct {
	AccountID      uint    `json:"accountId"`
	DocumentNumber string  `json:"documentNumber"`
	Balance        float64 `json:"balance"`
}

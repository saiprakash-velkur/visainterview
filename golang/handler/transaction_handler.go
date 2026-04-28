package handler

import (
	"errors"
	"net/http"

	"github.com/gin-gonic/gin"
	"github.com/visainterview/financial-transaction-system/dto"
	"github.com/visainterview/financial-transaction-system/service"
	"github.com/visainterview/financial-transaction-system/utils"
)

type TransactionHandler struct {
	transactionService service.TransactionService
}

func NewTransactionHandler(transactionService service.TransactionService) *TransactionHandler {
	return &TransactionHandler{transactionService: transactionService}
}

func (h *TransactionHandler) CreateTransaction(c *gin.Context) {
	var req dto.CreateTransactionRequest
	if err := c.ShouldBindJSON(&req); err != nil {
		c.JSON(http.StatusBadRequest, gin.H{"code": "INVALID_REQUEST", "message": err.Error()})
		return
	}

	resp, err := h.transactionService.CreateTransaction(c.Request.Context(), req)
	if err != nil {
		if errors.Is(err, utils.ErrIdempotencyKeyExists) {
			c.JSON(http.StatusCreated, resp)
			return
		}
		c.Error(err)
		return
	}

	c.JSON(http.StatusCreated, resp)
}

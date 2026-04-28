package handler

import (
	"net/http"
	"strconv"

	"github.com/gin-gonic/gin"
	"github.com/visainterview/financial-transaction-system/dto"
	"github.com/visainterview/financial-transaction-system/service"
)

type AccountHandler struct {
	accountService service.AccountService
}

func NewAccountHandler(accountService service.AccountService) *AccountHandler {
	return &AccountHandler{accountService: accountService}
}

func (h *AccountHandler) CreateAccount(c *gin.Context) {
	var req dto.CreateAccountRequest
	if err := c.ShouldBindJSON(&req); err != nil {
		c.JSON(http.StatusBadRequest, gin.H{"code": "INVALID_REQUEST", "message": err.Error()})
		return
	}

	resp, err := h.accountService.CreateAccount(c.Request.Context(), req)
	if err != nil {
		c.Error(err)
		return
	}

	c.JSON(http.StatusCreated, resp)
}

func (h *AccountHandler) GetAccount(c *gin.Context) {
	accountID, err := strconv.ParseUint(c.Param("accountId"), 10, 32)
	if err != nil {
		c.JSON(http.StatusBadRequest, gin.H{"code": "INVALID_ACCOUNT_ID", "message": "Invalid account ID"})
		return
	}

	resp, err := h.accountService.GetAccount(c.Request.Context(), uint(accountID))
	if err != nil {
		c.Error(err)
		return
	}

	c.JSON(http.StatusOK, resp)
}

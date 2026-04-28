package middleware

import (
	"errors"
	"net/http"

	"github.com/gin-gonic/gin"
	"github.com/visainterview/financial-transaction-system/utils"
)

func ErrorHandler() gin.HandlerFunc {
	return func(c *gin.Context) {
		c.Next()

		if len(c.Errors) > 0 {
			err := c.Errors.Last().Err
			
			switch {
			case errors.Is(err, utils.ErrDocumentNumberExists):
				c.JSON(http.StatusConflict, utils.NewErrorResponse("DOCUMENT_EXISTS", err.Error()))
			case errors.Is(err, utils.ErrDocumentNumberRequired):
				c.JSON(http.StatusBadRequest, utils.NewErrorResponse("DOCUMENT_REQUIRED", err.Error()))
			case errors.Is(err, utils.ErrAccountNotFound):
				c.JSON(http.StatusNotFound, utils.NewErrorResponse("ACCOUNT_NOT_FOUND", err.Error()))
			case errors.Is(err, utils.ErrInvalidTransactionType):
				c.JSON(http.StatusBadRequest, utils.NewErrorResponse("INVALID_TRANSACTION_TYPE", err.Error()))
			case errors.Is(err, utils.ErrInsufficientBalance):
				c.JSON(http.StatusUnprocessableEntity, utils.NewErrorResponse("INSUFFICIENT_BALANCE", err.Error()))
			case errors.Is(err, utils.ErrIdempotencyKeyExists):
				return
			case errors.Is(err, utils.ErrOptimisticLockFailed):
				c.JSON(http.StatusConflict, utils.NewErrorResponse("OPTIMISTIC_LOCK_FAILED", err.Error()))
			default:
				c.JSON(http.StatusInternalServerError, utils.NewErrorResponse("INTERNAL_ERROR", "An internal error occurred"))
			}
		}
	}
}

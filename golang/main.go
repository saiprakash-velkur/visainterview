package main

import (
	"fmt"
	"log"

	"github.com/gin-gonic/gin"
	"github.com/visainterview/financial-transaction-system/config"
	"github.com/visainterview/financial-transaction-system/database"
	"github.com/visainterview/financial-transaction-system/handler"
	"github.com/visainterview/financial-transaction-system/middleware"
	"github.com/visainterview/financial-transaction-system/repository"
	"github.com/visainterview/financial-transaction-system/service"
)

func main() {
	cfg, err := config.Load()
	if err != nil {
		log.Fatalf("Failed to load config: %v", err)
	}

	if err := database.RunMigrations(&cfg.Database); err != nil {
		log.Fatalf("Failed to run migrations: %v", err)
	}
	log.Println("Migrations completed successfully")

	db, err := database.InitDB(&cfg.Database)
	if err != nil {
		log.Fatalf("Failed to initialize database: %v", err)
	}

	accountRepo := repository.NewAccountRepository(db)
	transactionRepo := repository.NewTransactionRepository(db)

	accountService := service.NewAccountService(accountRepo)
	transactionService := service.NewTransactionService(transactionRepo, accountRepo)

	accountHandler := handler.NewAccountHandler(accountService)
	transactionHandler := handler.NewTransactionHandler(transactionService)
	healthHandler := handler.NewHealthHandler()
	metricsHandler := handler.NewMetricsHandler()

	router := gin.Default()
	router.Use(middleware.PrometheusMiddleware())
	router.Use(middleware.ErrorHandler())

	router.GET("/health", healthHandler.HealthCheck)
	router.GET("/metrics", metricsHandler.PrometheusMetrics)
	router.GET("/metrics/runtime", metricsHandler.RuntimeStats)
	router.POST("/accounts", accountHandler.CreateAccount)
	router.GET("/accounts/:accountId", accountHandler.GetAccount)
	router.POST("/transactions", transactionHandler.CreateTransaction)

	addr := fmt.Sprintf(":%s", cfg.Server.Port)
	log.Printf("Starting server on %s", addr)
	if err := router.Run(addr); err != nil {
		log.Fatalf("Failed to start server: %v", err)
	}
}

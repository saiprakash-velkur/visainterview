.PHONY: run migrate-up migrate-down docker-up docker-down build docker-build docker-run

run:
	go run main.go

build:
	go build -o bin/financial-transaction-system main.go

docker-build:
	docker build -t financial-transaction-system:latest .

docker-run:
	docker run -p 8080:8080 \
		-e APP_DATABASE_HOST=host.docker.internal \
		-e APP_DATABASE_PORT=5432 \
		-e APP_DATABASE_USER=postgres \
		-e APP_DATABASE_PASSWORD=postgres \
		-e APP_DATABASE_DBNAME=financial_db \
		-e APP_DATABASE_SSLMODE=disable \
		financial-transaction-system:latest

docker-up:
	docker compose up -d

docker-down:
	docker compose down

migrate-up:
	migrate -path migrations -database "postgresql://postgres:postgres@localhost:5432/financial_db?sslmode=disable" up

migrate-down:
	migrate -path migrations -database "postgresql://postgres:postgres@localhost:5432/financial_db?sslmode=disable" down

deps:
	go mod download
	go mod tidy

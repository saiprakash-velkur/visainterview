# Financial Transaction System

Production-grade GoLang REST API for financial transactions with multi-layered architecture.

## Architecture

- **Handler Layer**: HTTP request handling (AccountHandler, TransactionHandler)
- **Service Layer**: Business logic (AccountService, TransactionService)
- **Repository Layer**: Data access (AccountRepository, TransactionRepository)
- **Models**: Domain entities (Customer, Account, Transaction)
- **Middleware**: Error handling, request tracing
- **Config**: Viper-based configuration management
- **Utils**: Shared utilities and error definitions

## Tech Stack

- Gin Framework
- GORM
- Viper
- PostgreSQL
- Docker Compose
- golang-migrate

## Monitoring & Observability

### Metrics Endpoints

- `GET /metrics` - Prometheus metrics (scrape endpoint)
- `GET /metrics/runtime` - Runtime stats (goroutines, memory, GC)
- `GET /health` - Health check

### Available Metrics

**System Metrics:**
- `go_goroutines` - Active goroutine count
- `go_memstats_alloc_bytes` - Memory allocated
- `go_gc_duration_seconds` - GC pause duration
- `go_threads` - OS thread count

**Application Metrics:**
- `http_requests_total` - Total HTTP requests by method, endpoint, status
- `http_request_duration_seconds` - Request latency histogram
- `account_creation_total` - Total accounts created
- `transaction_total` - Total transactions by type and status
- `transaction_amount` - Transaction amount histogram
- `optimistic_lock_retries_total` - Optimistic lock retry count

### Prometheus Setup

```yaml
# prometheus.yml
scrape_configs:
  - job_name: 'financial-api'
    static_configs:
      - targets: ['localhost:8080']
    metrics_path: '/metrics'
```

### Grafana Dashboard

Import `grafana-dashboard.json` for pre-configured visualizations:
- Goroutine count over time
- HTTP request rate and latency
- Transaction metrics by type
- Memory usage and GC activity
- Optimistic lock retry rate

## Configuration

### Priority Order (Highest to Lowest)

1. **Environment Variables** (Kubernetes/Docker)
2. **config.yaml** (Local development)

### Environment Variables for Kubernetes/Docker

```bash
APP_SERVER_PORT=8080
APP_DATABASE_HOST=postgres-service
APP_DATABASE_PORT=5432
APP_DATABASE_USER=postgres
APP_DATABASE_PASSWORD=secretpassword
APP_DATABASE_DBNAME=financial_db
APP_DATABASE_SSLMODE=disable
```

### Kubernetes Deployment Example

```yaml
apiVersion: apps/v1
kind: Deployment
metadata:
  name: financial-api
spec:
  template:
    spec:
      containers:
      - name: financial-api
        image: financial-api:latest
        env:
        - name: APP_DATABASE_HOST
          value: "postgres-service"
        - name: APP_DATABASE_PASSWORD
          valueFrom:
            secretKeyRef:
              name: db-secret
              key: password
```

## Setup

### Option 1: Automatic Migrations (Recommended for Docker)

Migrations run automatically on application startup (like Flyway in Spring Boot).

```bash
# Start PostgreSQL
make docker-up

# Install dependencies and run (migrations run automatically)
make deps
make run
```

### Option 2: Manual Migrations

If you prefer to run migrations manually:

```bash
# Start PostgreSQL
make docker-up

# Run migrations manually
make migrate-up

# Install dependencies
make deps

# Run application
make run
```

## API Endpoints

### 1. Create Account
```bash
POST /accounts
{
  "documentNumber": "12345678900"
}
```

### 2. Get Account
```bash
GET /accounts/{accountId}
```

### 3. Create Transaction
```bash
POST /transactions
{
  "accountId": 1,
  "transactionType": 4,
  "amount": 123.45,
  "idempotencyKey": "<uuid>"
}
```

## Transaction Types

1. Normal Purchase (Debit)
2. Purchase with Installments (Not implemented)
3. Withdrawal (Debit)
4. Credit Voucher (Credit)

## Features

- Optimistic locking for concurrent transactions
- Idempotency key support
- Atomic customer and account creation
- Balance validation
- Comprehensive error handling

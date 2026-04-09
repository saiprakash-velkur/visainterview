# Card Transaction System

A Spring Boot monolith application for managing card transactions with account creation and transaction processing.

## Tech Stack
- Java 17
- Spring Boot 3.x
- Spring Data JPA
- PostgreSQL
- Lombok
- Hibernate (for schema management)

## Prerequisites
- Java 17
- Maven
- Docker
- Docker compose

## Setup

### Option 1: Using Docker Compose (Recommended)
```bash
docker-compose up -d
```

The application will start on `http://localhost:8080`

### Option 2: Manual Setup

##### 1. Start PostgreSQL Database
```bash
docker run --name postgres-card \
  -e POSTGRES_PASSWORD=postgres \
  -e POSTGRES_USER=postgres \
  -e POSTGRES_DB=cardtransaction \
  -p 5432:5432 \
  -d postgres:latest
```

#### 2. Build and Run Application
```bash
mvn clean install
mvn spring-boot:run
```

The application will start on `http://localhost:8080`

## Docker Deployment

See [DOCKER.md](DOCKER.md) for detailed Docker build, run, and deployment instructions.

### Quick Docker Commands
```bash
# Build image
docker build -t card-transaction-system:latest .

# Tag for Docker Hub
docker tag card-transaction-system:latest prakash971/visainterview-app:1.0

# Push to Docker Hub
docker push prakash971/visainterview-app:1.0

# Run with docker-compose (uses pre-built image)
docker-compose up -d
```

## API Endpoints

### 1. Create Account
**POST** `/accounts`

Creates a customer and their first account atomically.

**Request:**
```json
{
  "documentNumber": "12345678900"
}
```

**Response (201):**
```json
{
  "accountId": 1,
  "documentNumber": "12345678900",
  "balance": 0.00
}
```

**Errors:**
- `409 CONFLICT` - Document number already exists
- `400 BAD_REQUEST` - Document number not provided

### 2. Get Account
**GET** `/accounts/{accountId}`

Retrieves account details by account ID.

**Response (200):**
```json
{
  "accountId": 1,
  "balance": 100.50,
  "documentNumber": "12345678900"
}
```

**Errors:**
- `404 NOT_FOUND` - Account not found

### 3. Create Transaction
**POST** `/transactions`

Creates a transaction and updates account balance based on transaction type.

**Request:**
```json
{
  "accountId": 1,
  "transactionType": 4,
  "amount": 123.45,
  "idempotencyKey": "550e8400-e29b-41d4-a716-446655440000"
}
```

**Response (201):**
```json
{
  "transactionId": 1,
  "accountId": 1,
  "transactionType": 4,
  "amount": 123.45,
  "transactionDate": "2024-01-15T10:30:00"
}
```

**Errors:**
- `404 NOT_FOUND` - Account not found
- `400 BAD_REQUEST` - Invalid transaction type
- `400 BAD_REQUEST` - Insufficient balance
- `409 CONFLICT` - Idempotency key already exists (returns existing transaction)
- `409 CONFLICT` - Concurrent update (optimistic locking failure)

## Transaction Types
- `1` - Normal Purchase (Debit)
- `2` - Purchase with Installments (Not implemented)
- `3` - Withdrawal (Debit)
- `4` - Credit Voucher (Credit)

## Architecture

### Layers
- **Controller**: REST API endpoints
- **Service**: Business logic
- **Repository**: Data access layer
- **Entity**: JPA entities
- **DTO**: Data transfer objects
- **Exception**: Custom exceptions and global error handling

### Key Features
- Optimistic locking for concurrent transaction handling
- Idempotency support for transactions
- Atomic customer and account creation
- Audit fields (created_at, updated_at) on all entities
- Comprehensive error handling with proper HTTP status codes

## Database Schema

### Customers
- id (PK)
- document_number (unique, indexed)
- name
- created_at
- updated_at

### Accounts
- id (PK)
- customer_id (FK)
- balance (default 0, never negative)
- version (optimistic locking)
- created_at
- updated_at

### Transactions
- id (PK)
- account_id (FK)
- transaction_type
- amount
- idempotency_key (unique)
- created_at
- updated_at

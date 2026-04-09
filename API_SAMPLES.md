# Card Transaction System - Sample API Requests

## 1. Create Account
```bash
curl -X POST http://localhost:8080/accounts \
  -H "Content-Type: application/json" \
  -d '{
    "documentNumber": "12345678900"
  }'
```

Expected Response (201):
```json
{
  "accountId": 1,
  "documentNumber": "12345678900",
  "balance": 0.00
}
```

## 2. Get Account
```bash
curl -X GET http://localhost:8080/accounts/1
```

Expected Response (200):
```json
{
  "accountId": 1,
  "balance": 0.00,
  "documentNumber": "12345678900"
}
```

## 3. Create Transaction - Credit Voucher
```bash
curl -X POST http://localhost:8080/transactions \
  -H "Content-Type: application/json" \
  -d '{
    "accountId": 1,
    "transactionType": 4,
    "amount": 500.00,
    "idempotencyKey": "550e8400-e29b-41d4-a716-446655440001"
  }'
```

Expected Response (201):
```json
{
  "transactionId": 1,
  "accountId": 1,
  "transactionType": 4,
  "amount": 500.00,
  "transactionDate": "2024-01-15T10:30:00"
}
```

## 4. Create Transaction - Normal Purchase
```bash
curl -X POST http://localhost:8080/transactions \
  -H "Content-Type: application/json" \
  -d '{
    "accountId": 1,
    "transactionType": 1,
    "amount": 100.50,
    "idempotencyKey": "550e8400-e29b-41d4-a716-446655440002"
  }'
```

## 5. Create Transaction - Withdrawal
```bash
curl -X POST http://localhost:8080/transactions \
  -H "Content-Type: application/json" \
  -d '{
    "accountId": 1,
    "transactionType": 3,
    "amount": 50.00,
    "idempotencyKey": "550e8400-e29b-41d4-a716-446655440003"
  }'
```

## Error Scenarios

### Duplicate Document Number
```bash
curl -X POST http://localhost:8080/accounts \
  -H "Content-Type: application/json" \
  -d '{
    "documentNumber": "12345678900"
  }'
```

Expected Response (409):
```json
{
  "errorCode": "DUPLICATE_DOCUMENT_NUMBER",
  "message": "Document number already exists"
}
```

### Account Not Found
```bash
curl -X GET http://localhost:8080/accounts/999
```

Expected Response (404):
```json
{
  "errorCode": "ACCOUNT_NOT_FOUND",
  "message": "Account not found"
}
```

### Insufficient Balance
```bash
curl -X POST http://localhost:8080/transactions \
  -H "Content-Type: application/json" \
  -d '{
    "accountId": 1,
    "transactionType": 1,
    "amount": 10000.00,
    "idempotencyKey": "550e8400-e29b-41d4-a716-446655440004"
  }'
```

Expected Response (400):
```json
{
  "errorCode": "INSUFFICIENT_BALANCE",
  "message": "Insufficient balance"
}
```

### Invalid Transaction Type
```bash
curl -X POST http://localhost:8080/transactions \
  -H "Content-Type: application/json" \
  -d '{
    "accountId": 1,
    "transactionType": 99,
    "amount": 100.00,
    "idempotencyKey": "550e8400-e29b-41d4-a716-446655440005"
  }'
```

Expected Response (400):
```json
{
  "errorCode": "INVALID_TRANSACTION_TYPE",
  "message": "Invalid transaction type: 99"
}
```

### Idempotency Check (Duplicate Request)
```bash
# Send the same request twice with same idempotencyKey
curl -X POST http://localhost:8080/transactions \
  -H "Content-Type: application/json" \
  -d '{
    "accountId": 1,
    "transactionType": 4,
    "amount": 100.00,
    "idempotencyKey": "550e8400-e29b-41d4-a716-446655440001"
  }'
```

Expected Response (201): Returns the existing transaction data

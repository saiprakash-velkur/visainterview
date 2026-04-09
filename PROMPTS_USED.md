Assume the role of a Senior Java Backend engineer. Output of the task must be:
1.  Maintainable
2. Testable
3. Simple

After this task, I will use this as a base for adding more features further. I want a product-grade code that uses correct design patterns and keep it modular.

Problem summary:
Build a Spring Boot monolith for a card transaction system with three endpoints:
- Create an account
- Retrieve an account
- Create a transaction

Transactions are typed (Normal purchase, Purchase with installments, Withdrawal, Credit Voucher).

Right now, we will only focus on just Normal Purchase and Withdrawal and Credit Voucher. We will still have the transaction type of "Purchase with installments" setup, but we won't build anything around it.

Lets assign numbers to the transaction types for ease - NormalPurchase:1, PurchaseWithInstallments:2, Withdrawal: 3, CreditVoucher:4 and create these as ENUM

1&3 types reduce account balance, 4 increase account balance. Debit transactions will not process if the balance is insufficient.

-----------------------
TechStack I want to use: Java17, spring data jpa as ORM. use lombok for reducing boilerplate code, Postgres for DB, We'll use hibernate to do the migrations as this is just a sample base and can include flyway if needed later. I will use postgres docker image for this to connect. Lets keep the logic modular with controller, service, repo layers separately. No need for interfaces, lets just go with solid implementations.

Here are the entities:
1. Customer
2. Account
3. Transaction

Customer: (document_number must be available from request)
- id
- document_number (unique, indexed)
- name (optional)
- audit details via common abstraction that adds it to every entity

Account: 
- id
- customer (one to one relation with account)
- balance (default 0 and can never go to negative)
- audit fields
-version (for optimistic locking)

Transaction:
- id
- account (many to one)
- transactiontype
- amount
- idempotencykey (provided in request, uuid, unique)
- audit details

API Contracts:
I need two different controllers - account controller and transaction controller and all the following layers (service and repo) corresponding to them separated.

1. POST /accounts
Creates a customer and their first account atomically.
RequestBody:
{
  "documentNumber": "12345678900"
}
Response: 201
{
  "accountId": 1,
  "documentNumber": <doc number received from req>,
  "balance": 0.00
}

errors: 
1. documentNumber already exists
2. documentNumber not available in request

2. GET /accounts/{accountId}
get account details based on accountId
Response: 200
{
  "accountId": "<account id>",
  "balance": <balance float value>,
"documentNumber>: <doc number>
}

errors: no matching account id (404)

3. POST /transactions
creates a transaction and based on the transactiontype, either debit or credit the balance
Request:
{
  "accountId": 1,
  "transactionType": 4,
  "amount": 123.45,
  "idempotencyKey": "<uuid>"
}

Response: 201
{
  "transactionId": <transactionid>,
  "accountId": <accountid>,
  "transactionType": 4,
  "amount": 123.45,
  "transactionDate": <createdtime from audit details>
}

errors: account id not exists(404), transactiontype (400) doesnt match, idempotencykey already exists (check for transaction idempotency and return existing transaction data by querying transaction table as response)
Insufficient balance

Include error code and an appropriate message in every error response.

Use optimistic locking for the requests while doing the transactions as we do not expect too many transactions per account concurrently.

assume postgres to be available at localhost:5432. No need to add any authentication and audit details are just created and updated time

================================

Prompt:2
Add index on accountId - for faster retrieval of accounts 
Add index on idempotencykey - for faster lookup

================================

Prompt:3
Create a docker file for this application

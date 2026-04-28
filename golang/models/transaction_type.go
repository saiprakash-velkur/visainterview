package models

type TransactionType int

const (
	NormalPurchase TransactionType = 1
	PurchaseWithInstallments TransactionType = 2
	Withdrawal TransactionType = 3
	CreditVoucher TransactionType = 4
)

func (t TransactionType) IsValid() bool {
	return t >= 1 && t <= 4
}

func (t TransactionType) IsDebit() bool {
	return t == NormalPurchase || t == Withdrawal
}

func (t TransactionType) IsCredit() bool {
	return t == CreditVoucher
}

func (t TransactionType) String() string {
	switch t {
	case NormalPurchase:
		return "normal_purchase"
	case PurchaseWithInstallments:
		return "purchase_installments"
	case Withdrawal:
		return "withdrawal"
	case CreditVoucher:
		return "credit_voucher"
	default:
		return "unknown"
	}
}

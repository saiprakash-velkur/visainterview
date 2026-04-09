package com.example.visainterview.entity;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class TransactionTypeTest {

    @Test
    void fromCode_ValidCodes_ReturnsCorrectType() {
        assertEquals(TransactionType.NORMAL_PURCHASE, TransactionType.fromCode(1));
        assertEquals(TransactionType.PURCHASE_WITH_INSTALLMENTS, TransactionType.fromCode(2));
        assertEquals(TransactionType.WITHDRAWAL, TransactionType.fromCode(3));
        assertEquals(TransactionType.CREDIT_VOUCHER, TransactionType.fromCode(4));
    }

    @Test
    void fromCode_InvalidCode_ThrowsException() {
        assertThrows(IllegalArgumentException.class, () -> {
            TransactionType.fromCode(99);
        });
    }

    @Test
    void isDebit_NormalPurchase_ReturnsTrue() {
        assertTrue(TransactionType.NORMAL_PURCHASE.isDebit());
    }

    @Test
    void isDebit_Withdrawal_ReturnsTrue() {
        assertTrue(TransactionType.WITHDRAWAL.isDebit());
    }

    @Test
    void isDebit_CreditVoucher_ReturnsFalse() {
        assertFalse(TransactionType.CREDIT_VOUCHER.isDebit());
    }

    @Test
    void isDebit_PurchaseWithInstallments_ReturnsFalse() {
        assertFalse(TransactionType.PURCHASE_WITH_INSTALLMENTS.isDebit());
    }

    @Test
    void isCredit_CreditVoucher_ReturnsTrue() {
        assertTrue(TransactionType.CREDIT_VOUCHER.isCredit());
    }

    @Test
    void isCredit_NormalPurchase_ReturnsFalse() {
        assertFalse(TransactionType.NORMAL_PURCHASE.isCredit());
    }

    @Test
    void isCredit_Withdrawal_ReturnsFalse() {
        assertFalse(TransactionType.WITHDRAWAL.isCredit());
    }

    @Test
    void getCode_ReturnsCorrectCode() {
        assertEquals(1, TransactionType.NORMAL_PURCHASE.getCode());
        assertEquals(2, TransactionType.PURCHASE_WITH_INSTALLMENTS.getCode());
        assertEquals(3, TransactionType.WITHDRAWAL.getCode());
        assertEquals(4, TransactionType.CREDIT_VOUCHER.getCode());
    }
}

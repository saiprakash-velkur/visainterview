package com.example.visainterview.entity;

import lombok.Getter;

@Getter
public enum TransactionType {
    NORMAL_PURCHASE(1),
    PURCHASE_WITH_INSTALLMENTS(2),
    WITHDRAWAL(3),
    CREDIT_VOUCHER(4);

    private final int code;

    TransactionType(int code) {
        this.code = code;
    }

    public static TransactionType fromCode(int code) {
        for (TransactionType type : values()) {
            if (type.code == code) {
                return type;
            }
        }
        throw new IllegalArgumentException("Invalid transaction type code: " + code);
    }

    public boolean isDebit() {
        return this == NORMAL_PURCHASE || this == WITHDRAWAL;
    }

    public boolean isCredit() {
        return this == CREDIT_VOUCHER;
    }
}

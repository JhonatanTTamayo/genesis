package com.breaze.genesis.entity.tokens;

public enum TokenTransactionReferenceType {
    OPERATION_EXECUTION,
    SUBSCRIPTION,
    ADMIN_RECHARGE;

    public boolean isTokenAdditionMovement() {
        return this == SUBSCRIPTION || this == ADMIN_RECHARGE;
    }

    public boolean isTokenSubtractionMovement() {
        return this == OPERATION_EXECUTION;
    }
}

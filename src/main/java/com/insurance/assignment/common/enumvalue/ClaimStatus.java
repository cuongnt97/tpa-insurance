package com.insurance.assignment.common.enumvalue;

public enum ClaimStatus {
    SUBMITTED,
    APPROVED,
    REJECTED;

    public boolean isTerminal() {
        return this == APPROVED || this == REJECTED;
    }
}

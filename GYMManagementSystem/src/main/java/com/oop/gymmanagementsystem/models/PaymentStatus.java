package com.oop.gymmanagementsystem.models;

import java.io.Serializable;

public enum PaymentStatus implements Serializable {
    PAID("Paid"),
    UNPAID("Unpaid"),
    PARTIAL("Partial");

    private final String displayName;

    PaymentStatus(String displayName) {
        this.displayName = displayName;
    }

    public String getDisplayName() { return displayName; }

    @Override
    public String toString() { return displayName; }
}

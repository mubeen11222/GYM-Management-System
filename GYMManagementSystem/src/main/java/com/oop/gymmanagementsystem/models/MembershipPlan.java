package com.oop.gymmanagementsystem.models;

import java.io.Serializable;

public enum MembershipPlan implements Serializable {
    BASIC("Basic", 3000.0, false),
    ADVANCED("Advanced", 10000.0, true);

    private final String displayName;
    private final double monthlyFee;
    private final boolean trainerRequired;

    MembershipPlan(String displayName, double monthlyFee, boolean trainerRequired) {
        this.displayName = displayName;
        this.monthlyFee = monthlyFee;
        this.trainerRequired = trainerRequired;
    }

    public String getDisplayName() { return displayName; }
    public double getMonthlyFee() { return monthlyFee; }
    public boolean isTrainerRequired() { return trainerRequired; }

    @Override
    public String toString() {
        return displayName + " (Rs. " + (int) monthlyFee + "/month)";
    }
}

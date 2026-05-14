package com.oop.gymmanagementsystem.models;

import java.io.Serializable;

public enum Role implements Serializable {
    ADMIN("Admin"),
    STAFF("Staff"),
    TRAINER("Trainer");

    private final String displayName;

    Role(String displayName) {
        this.displayName = displayName;
    }

    public String getDisplayName() {
        return displayName;
    }

    @Override
    public String toString() {
        return displayName;
    }
}

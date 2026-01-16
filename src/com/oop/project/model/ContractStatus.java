package com.oop.project.model;

public enum ContractStatus {
    DRAFT("Draft"),
    ACTIVE("Active"),
    RETURNED("Returned"),
    COMPLETED("Completed"),
    OVERDUE("Overdue");

    private final String displayName;

    ContractStatus(String displayName) {
        this.displayName = displayName;
    }

    public String getDisplayName() {
        return displayName;
    }

    @Override
    public String toString() {
        return displayName;
    }

    public static ContractStatus fromString(String text) {
        if (text == null || text.trim().isEmpty()) {
            return DRAFT;
        }
        for (ContractStatus s : values()) {
            if (s.name().equalsIgnoreCase(text.trim()) || s.displayName.equalsIgnoreCase(text.trim())) {
                return s;
            }
        }
        return DRAFT;
    }
}

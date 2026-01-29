package com.oop.project.model;

public enum ItemStatus {
    AVAILABLE("Available"),
    UNAVAILABLE("Unavailable");

    private final String displayName;

    ItemStatus(String displayName) {
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

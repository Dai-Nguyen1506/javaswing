package com.oop.project.model;

public enum EquipmentCondition {
    GOOD("Good"),
    NEEDS_REPAIR("Needs Repair"),
    OUT_OF_SERVICE("Out of Service");

    private final String displayName;

    EquipmentCondition(String displayName) {
        this.displayName = displayName;
    }

    public String getDisplayName() {
        return displayName;
    }

    @Override
    public String toString() {
        return displayName;
    }

    public static EquipmentCondition fromString(String text) {
        if (text == null || text.trim().isEmpty()) {
            return GOOD;
        }
        for (EquipmentCondition c : values()) {
            if (c.name().equalsIgnoreCase(text.trim()) || c.displayName.equalsIgnoreCase(text.trim())) {
                return c;
            }
        }
        return GOOD;
    }
}

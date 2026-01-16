package com.oop.project.model;

public enum Role {
    ADMIN,
    STAFF;

    public static Role fromString(String raw) {
        if (raw == null) {
            return STAFF;
        }
        return Role.valueOf(raw.trim().toUpperCase());
    }
}

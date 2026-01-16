package com.oop.project.model;

import java.util.Objects;

public final class User {
    private final String username;
    private final Role role;
    private final String saltHex;
    private final String passwordHashHex;

    public User(String username, Role role, String saltHex, String passwordHashHex) {
        this.username = Objects.requireNonNull(username, "username").trim();
        this.role = Objects.requireNonNull(role, "role");
        this.saltHex = Objects.requireNonNull(saltHex, "saltHex");
        this.passwordHashHex = Objects.requireNonNull(passwordHashHex, "passwordHashHex");
    }

    public String getUsername() {
        return username;
    }

    public Role getRole() {
        return role;
    }

    public String getSaltHex() {
        return saltHex;
    }

    public String getPasswordHashHex() {
        return passwordHashHex;
    }
}

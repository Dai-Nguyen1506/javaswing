package com.oop.project.service;

import com.oop.project.model.User;

import java.util.Optional;

public final class Session {
    private static volatile User currentUser;

    private Session() {
    }

    public static Optional<User> currentUser() {
        return Optional.ofNullable(currentUser);
    }

    public static void setCurrentUser(User user) {
        currentUser = user;
    }

    public static void clear() {
        currentUser = null;
    }
}

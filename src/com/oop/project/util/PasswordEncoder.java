package com.oop.project.util;

import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.security.SecureRandom;

public final class PasswordEncoder {
    private static final SecureRandom RNG = new SecureRandom();

    private PasswordEncoder() {
    }

    public static String newSaltHex() {
        byte[] salt = new byte[16];
        RNG.nextBytes(salt);
        return Hex.toHex(salt);
    }

    public static String hashPasswordHex(String saltHex, String password) {
        byte[] salt = Hex.fromHex(saltHex);
        byte[] pwd = password.getBytes(StandardCharsets.UTF_8);
        byte[] combined = new byte[salt.length + pwd.length];
        System.arraycopy(salt, 0, combined, 0, salt.length);
        System.arraycopy(pwd, 0, combined, salt.length, pwd.length);

        try {
            MessageDigest digest = MessageDigest.getInstance("SHA-256");
            return Hex.toHex(digest.digest(combined));
        } catch (NoSuchAlgorithmException e) {
            throw new IllegalStateException("SHA-256 not available", e);
        }
    }
}

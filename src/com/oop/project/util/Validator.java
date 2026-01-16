package com.oop.project.util;

import java.util.regex.Pattern;

public final class Validator {
    private static final Pattern EMAIL_PATTERN = Pattern.compile(
        "^[A-Za-z0-9+_.-]+@[A-Za-z0-9.-]+\\.[A-Za-z]{2,}$"
    );
    
    private static final Pattern PHONE_PATTERN = Pattern.compile(
        "^[0-9]{10,15}$"
    );
    
    private static final Pattern CONTRACT_NUMBER_PATTERN = Pattern.compile(
        "^[A-Z][0-9]{3}$"
    );

    private Validator() {
    }

    public static boolean isValidEmail(String email) {
        return email != null && EMAIL_PATTERN.matcher(email.trim()).matches();
    }

    public static boolean isValidPhone(String phone) {
        if (phone == null) {
            return false;
        }
        String cleaned = phone.replaceAll("[\\s()-]", "");
        return PHONE_PATTERN.matcher(cleaned).matches();
    }

    public static boolean isValidContractNumber(String contractNumber) {
        return contractNumber != null && CONTRACT_NUMBER_PATTERN.matcher(contractNumber.trim()).matches();
    }

    public static boolean isValidDuration(int minutes) {
        return minutes >= 60 && minutes <= 7200;
    }
}

package com.oop.project.util;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;

public final class AppPaths {
    private AppPaths() {
    }

    public static Path dataDir() {
        return Path.of("data");
    }

    public static Path usersFile() {
        return dataDir().resolve("users.csv");
    }

    public static Path auditLogFile() {
        return dataDir().resolve("audit.log");
    }

    public static Path customersFile() {
        return dataDir().resolve("customers.dat");
    }

    public static Path equipmentFile() {
        return dataDir().resolve("equipment.dat");
    }

    public static Path lessonPackagesFile() {
        return dataDir().resolve("lesson_packages.dat");
    }

    public static Path instructorsFile() {
        return dataDir().resolve("instructors.dat");
    }

    public static Path rentalContractsFile() {
        return dataDir().resolve("rental_contracts.dat");
    }

    public static void ensureDataDirectories() {
        try {
            Files.createDirectories(dataDir());
        } catch (IOException e) {
            throw new IllegalStateException("Cannot create data directory: " + dataDir().toAbsolutePath(), e);
        }
    }
}

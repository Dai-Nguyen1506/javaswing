package com.oop.project.repository.file;

import com.oop.project.model.Role;
import com.oop.project.model.User;
import com.oop.project.repository.UserRepository;
import com.oop.project.util.AppPaths;
import com.oop.project.util.Csv;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.StandardOpenOption;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

public class FileUserRepository implements UserRepository {
    private final Path filePath;

    public FileUserRepository() {
        this(AppPaths.usersFile());
    }

    public FileUserRepository(Path filePath) {
        this.filePath = filePath;
    }

    @Override
    public Optional<User> findByUsername(String username) {
        return findAll().stream()
                .filter(u -> u.getUsername().equalsIgnoreCase(username == null ? "" : username.trim()))
                .findFirst();
    }

    @Override
    public List<User> findAll() {
        if (!Files.exists(filePath)) {
            return List.of();
        }
        try {
            List<String> lines = Files.readAllLines(filePath, StandardCharsets.UTF_8);
            List<User> users = new ArrayList<>();
            for (String line : lines) {
                if (line.isBlank() || line.startsWith("#")) {
                    continue;
                }
                List<String> cols = Csv.parseLine(line);
                if (cols.size() < 4) {
                    continue;
                }
                String username = cols.get(0);
                Role role = Role.fromString(cols.get(1));
                String saltHex = cols.get(2);
                String hashHex = cols.get(3);
                users.add(new User(username, role, saltHex, hashHex));
            }
            return users;
        } catch (IOException e) {
            throw new IllegalStateException("Cannot read users file: " + filePath.toAbsolutePath(), e);
        }
    }

    @Override
    public void save(User user) {
        List<User> existing = new ArrayList<>(findAll());
        existing.removeIf(u -> u.getUsername().equalsIgnoreCase(user.getUsername()));
        existing.add(user);

        List<String> lines = new ArrayList<>();
        lines.add("# username,role,saltHex,passwordHashHex");
        for (User u : existing) {
            lines.add(Csv.escape(u.getUsername()) + "," + Csv.escape(u.getRole().name()) + "," + Csv.escape(u.getSaltHex()) + "," + Csv.escape(u.getPasswordHashHex()));
        }

        try {
            Files.write(filePath, lines, StandardCharsets.UTF_8,
                    StandardOpenOption.CREATE, StandardOpenOption.TRUNCATE_EXISTING);
        } catch (IOException e) {
            throw new IllegalStateException("Cannot write users file: " + filePath.toAbsolutePath(), e);
        }
    }
}

package com.oop.project.repository.file;

import com.oop.project.model.LessonPackage;
import com.oop.project.repository.LessonPackageRepository;
import com.oop.project.util.AppPaths;

import java.io.*;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

public class FileLessonPackageRepository implements LessonPackageRepository {
    private final Path filePath;

    public FileLessonPackageRepository() {
        this(AppPaths.lessonPackagesFile());
    }

    public FileLessonPackageRepository(Path filePath) {
        this.filePath = filePath;
    }

    @Override
    public List<LessonPackage> findAll() {
        if (!Files.exists(filePath)) {
            return new ArrayList<>();
        }
        try (ObjectInputStream ois = new ObjectInputStream(new FileInputStream(filePath.toFile()))) {
            @SuppressWarnings("unchecked")
            List<LessonPackage> packages = (List<LessonPackage>) ois.readObject();
            return packages;
        } catch (EOFException e) {
            return new ArrayList<>();
        } catch (IOException | ClassNotFoundException e) {
            throw new IllegalStateException("Cannot read lesson packages file: " + filePath.toAbsolutePath(), e);
        }
    }

    @Override
    public Optional<LessonPackage> findById(String packageId) {
        return findAll().stream()
                .filter(p -> p.getPackageId().equalsIgnoreCase(packageId))
                .findFirst();
    }

    @Override
    public void save(LessonPackage lessonPackage) {
        List<LessonPackage> packages = findAll();
        packages.removeIf(p -> p.getPackageId().equalsIgnoreCase(lessonPackage.getPackageId()));
        packages.add(lessonPackage);
        saveAll(packages);
    }

    @Override
    public void delete(String packageId) {
        List<LessonPackage> packages = findAll();
        packages.removeIf(p -> p.getPackageId().equalsIgnoreCase(packageId));
        saveAll(packages);
    }

    private void saveAll(List<LessonPackage> packages) {
        try (ObjectOutputStream oos = new ObjectOutputStream(new FileOutputStream(filePath.toFile()))) {
            oos.writeObject(packages);
        } catch (IOException e) {
            throw new IllegalStateException("Cannot write lesson packages file: " + filePath.toAbsolutePath(), e);
        }
    }
}

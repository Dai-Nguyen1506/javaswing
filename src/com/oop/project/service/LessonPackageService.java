package com.oop.project.service;

import com.oop.project.exception.ValidationException;
import com.oop.project.model.LessonPackage;
import com.oop.project.repository.LessonPackageRepository;
import com.oop.project.repository.db.DbLessonPackageRepository;

import java.util.List;
import java.util.Optional;

public class LessonPackageService {
    private final LessonPackageRepository lessonPackageRepository;

    public LessonPackageService(LessonPackageRepository lessonPackageRepository) {
        this.lessonPackageRepository = lessonPackageRepository;
    }
    
    // Constructor for database mode
    public LessonPackageService() {
        this.lessonPackageRepository = new DbLessonPackageRepository();
    }

    public List<LessonPackage> getAllPackages() {
        return lessonPackageRepository.findAll();
    }
    
    // Alias for getAllPackages
    public List<LessonPackage> getAllLessons() {
        return getAllPackages();
    }
    
    // Get lesson by ID
    public LessonPackage getLessonById(String packageId) {
        return lessonPackageRepository.findById(packageId).orElse(null);
    }

    public Optional<LessonPackage> getPackageById(String packageId) {
        return lessonPackageRepository.findById(packageId);
    }

    public void createPackage(String packageId, String name, String description, int durationMinutes, double price, String instructorId) {
        validatePackage(packageId, name, durationMinutes, price);

        if (lessonPackageRepository.findById(packageId).isPresent()) {
            throw new ValidationException("Package ID already exists: " + packageId);
        }

        LessonPackage lessonPackage = new LessonPackage(packageId, name, description, durationMinutes, price, instructorId);
        lessonPackageRepository.save(lessonPackage);
    }

    public void updatePackage(String packageId, String name, String description, int durationMinutes, double price, String instructorId) {
        validatePackage(packageId, name, durationMinutes, price);

        if (lessonPackageRepository.findById(packageId).isEmpty()) {
            throw new ValidationException("Package not found: " + packageId);
        }

        LessonPackage lessonPackage = new LessonPackage(packageId, name, description, durationMinutes, price, instructorId);
        lessonPackageRepository.save(lessonPackage);
    }

    public void deletePackage(String packageId) {
        if (lessonPackageRepository.findById(packageId).isEmpty()) {
            throw new ValidationException("Package not found: " + packageId);
        }
        lessonPackageRepository.delete(packageId);
    }

    private void validatePackage(String packageId, String name, int durationMinutes, double price) {
        if (packageId == null || packageId.trim().isEmpty()) {
            throw new ValidationException("Package ID is required");
        }
        if (name == null || name.trim().isEmpty()) {
            throw new ValidationException("Package name is required");
        }
        if (durationMinutes <= 0) {
            throw new ValidationException("Duration must be positive");
        }
        if (price < 0) {
            throw new ValidationException("Price cannot be negative");
        }
    }
}

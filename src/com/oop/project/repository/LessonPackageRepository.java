package com.oop.project.repository;

import com.oop.project.model.LessonPackage;

import java.util.List;
import java.util.Optional;

public interface LessonPackageRepository {
    List<LessonPackage> findAll();
    Optional<LessonPackage> findById(String packageId);
    void save(LessonPackage lessonPackage);
    void delete(String packageId);
}

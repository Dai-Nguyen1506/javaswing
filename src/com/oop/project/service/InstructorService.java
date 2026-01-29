package com.oop.project.service;

import com.oop.project.model.Instructor;
import com.oop.project.model.LessonPackage;
import com.oop.project.model.RentalContract;
import com.oop.project.repository.db.DbInstructorRepository;
import com.oop.project.repository.db.DbLessonPackageRepository;
import com.oop.project.repository.db.DbRentalContractRepository;

import java.time.LocalDateTime;
import java.util.List;

/**
 * Service for managing instructors and checking schedule conflicts
 */
public class InstructorService {
    private final DbInstructorRepository instructorRepository;
    private final DbLessonPackageRepository lessonRepository;
    private final DbRentalContractRepository rentalRepository;

    public InstructorService() {
        this.instructorRepository = new DbInstructorRepository();
        this.lessonRepository = new DbLessonPackageRepository();
        this.rentalRepository = new DbRentalContractRepository();
    }

    /**
     * Get all instructors
     */
    public List<Instructor> getAllInstructors() {
        return instructorRepository.findAll();
    }

    /**
     * Find instructor by ID
     */
    public Instructor findById(String instructorId) {
        return instructorRepository.findById(instructorId).orElse(null);
    }

    /**
     * Get instructor name by lesson package ID
     */
    public String getInstructorNameByLessonId(String lessonPackageId) {
        if (lessonPackageId == null || lessonPackageId.isEmpty()) {
            return "N/A";
        }
        
        LessonPackage lesson = lessonRepository.findById(lessonPackageId).orElse(null);
        if (lesson == null || lesson.getInstructorId() == null) {
            return "N/A";
        }
        
        Instructor instructor = instructorRepository.findById(lesson.getInstructorId()).orElse(null);
        return instructor != null ? instructor.getName() : "N/A";
    }

    /**
     * Check if instructor has schedule conflict for the given time slot
     * Returns true if there is a conflict (instructor already booked)
     */
    public boolean hasScheduleConflict(String lessonPackageId, LocalDateTime startTime, int durationMinutes) {
        if (lessonPackageId == null || lessonPackageId.isEmpty() || startTime == null) {
            return false; // No lesson selected, no conflict
        }

        // Get the instructor for this lesson
        LessonPackage lesson = lessonRepository.findById(lessonPackageId).orElse(null);
        if (lesson == null || lesson.getInstructorId() == null) {
            return false; // No instructor assigned, no conflict
        }

        String instructorId = lesson.getInstructorId();
        LocalDateTime endTime = startTime.plusMinutes(durationMinutes);

        // Get all rental contracts with lessons taught by this instructor
        List<RentalContract> allRentals = rentalRepository.findAll();
        
        for (RentalContract rental : allRentals) {
            // Skip if no lesson package
            if (rental.getLessonPackageId() == null || rental.getLessonPackageId().isEmpty()) {
                continue;
            }

            // Get the lesson package for this rental
            LessonPackage rentalLesson = lessonRepository.findById(rental.getLessonPackageId()).orElse(null);
            if (rentalLesson == null || !instructorId.equals(rentalLesson.getInstructorId())) {
                continue; // Different instructor, no conflict
            }

            // Check time overlap
            LocalDateTime rentalStart = rental.getStartTime();
            LocalDateTime rentalEnd = rental.getEndTime();

            if (rentalStart == null || rentalEnd == null) {
                continue;
            }

            // Check if time slots overlap
            // Conflict occurs if: (newStart < existingEnd) AND (newEnd > existingStart)
            boolean overlaps = startTime.isBefore(rentalEnd) && endTime.isAfter(rentalStart);
            
            if (overlaps) {
                return true; // Conflict found
            }
        }

        return false; // No conflict
    }

    /**
     * Get conflict details message for display
     */
    public String getConflictDetails(String lessonPackageId, LocalDateTime startTime, int durationMinutes) {
        if (!hasScheduleConflict(lessonPackageId, startTime, durationMinutes)) {
            return null;
        }

        LessonPackage lesson = lessonRepository.findById(lessonPackageId).orElse(null);
        if (lesson == null) {
            return null;
        }

        Instructor instructor = instructorRepository.findById(lesson.getInstructorId()).orElse(null);
        String instructorName = instructor != null ? instructor.getName() : "Unknown";

        return String.format("Instructor %s is already booked during this time slot. Please choose a different time or instructor.", 
                            instructorName);
    }
}

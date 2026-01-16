package com.oop.project.model;

import java.io.Serializable;
import java.util.Objects;

public class LessonPackage implements Serializable {
    private static final long serialVersionUID = 1L;

    private String packageId;
    private String name;
    private String description;
    private int durationMinutes;
    private double price;
    private String instructorId;

    public LessonPackage() {
    }

    public LessonPackage(String packageId, String name, String description, int durationMinutes, double price, String instructorId) {
        this.packageId = packageId;
        this.name = name;
        this.description = description;
        this.durationMinutes = durationMinutes;
        this.price = price;
        this.instructorId = instructorId;
    }

    public String getPackageId() {
        return packageId;
    }

    public void setPackageId(String packageId) {
        this.packageId = packageId;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public int getDurationMinutes() {
        return durationMinutes;
    }

    public void setDurationMinutes(int durationMinutes) {
        this.durationMinutes = durationMinutes;
    }

    public double getPrice() {
        return price;
    }

    public void setPrice(double price) {
        this.price = price;
    }

    public String getInstructorId() {
        return instructorId;
    }

    public void setInstructorId(String instructorId) {
        this.instructorId = instructorId;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        LessonPackage that = (LessonPackage) o;
        return Objects.equals(packageId, that.packageId);
    }

    @Override
    public int hashCode() {
        return Objects.hash(packageId);
    }

    @Override
    public String toString() {
        return packageId + " - " + name + " ($" + price + ")";
    }
}

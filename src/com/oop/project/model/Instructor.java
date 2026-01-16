package com.oop.project.model;

import java.io.Serializable;
import java.util.Objects;

public class Instructor implements Serializable {
    private static final long serialVersionUID = 1L;

    private String instructorId;
    private String name;
    private String specialty;
    private String phone;

    public Instructor() {
    }

    public Instructor(String instructorId, String name, String specialty, String phone) {
        this.instructorId = instructorId;
        this.name = name;
        this.specialty = specialty;
        this.phone = phone;
    }

    public String getInstructorId() {
        return instructorId;
    }

    public void setInstructorId(String instructorId) {
        this.instructorId = instructorId;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getSpecialty() {
        return specialty;
    }

    public void setSpecialty(String specialty) {
        this.specialty = specialty;
    }

    public String getPhone() {
        return phone;
    }

    public void setPhone(String phone) {
        this.phone = phone;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Instructor that = (Instructor) o;
        return Objects.equals(instructorId, that.instructorId);
    }

    @Override
    public int hashCode() {
        return Objects.hash(instructorId);
    }

    @Override
    public String toString() {
        return instructorId + " - " + name;
    }
}

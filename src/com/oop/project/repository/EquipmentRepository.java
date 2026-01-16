package com.oop.project.repository;

import com.oop.project.model.Equipment;

import java.util.List;
import java.util.Optional;

public interface EquipmentRepository {
    List<Equipment> findAll();
    Optional<Equipment> findById(String equipmentId);
    void save(Equipment equipment);
    void delete(String equipmentId);
    List<Equipment> findByCategory(String category);
}

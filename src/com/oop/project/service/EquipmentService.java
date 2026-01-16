package com.oop.project.service;

import com.oop.project.exception.ValidationException;
import com.oop.project.model.Equipment;
import com.oop.project.model.EquipmentCondition;
import com.oop.project.repository.EquipmentRepository;

import java.util.List;
import java.util.Optional;

public class EquipmentService {
    private final EquipmentRepository equipmentRepository;

    public EquipmentService(EquipmentRepository equipmentRepository) {
        this.equipmentRepository = equipmentRepository;
    }

    public List<Equipment> getAllEquipment() {
        return equipmentRepository.findAll();
    }

    public Optional<Equipment> getEquipmentById(String equipmentId) {
        return equipmentRepository.findById(equipmentId);
    }

    public void createEquipment(String equipmentId, String name, String category, int quantity, EquipmentCondition condition) {
        validateEquipment(equipmentId, name, category, quantity);

        if (equipmentRepository.findById(equipmentId).isPresent()) {
            throw new ValidationException("Equipment ID already exists: " + equipmentId);
        }

        Equipment equipment = new Equipment(equipmentId, name, category, quantity, quantity, condition);
        equipmentRepository.save(equipment);
    }

    public void updateEquipment(String equipmentId, String name, String category, int quantity, int available, EquipmentCondition condition) {
        validateEquipment(equipmentId, name, category, quantity);

        if (equipmentRepository.findById(equipmentId).isEmpty()) {
            throw new ValidationException("Equipment not found: " + equipmentId);
        }

        if (available < 0 || available > quantity) {
            throw new ValidationException("Available quantity must be between 0 and total quantity");
        }

        Equipment equipment = new Equipment(equipmentId, name, category, quantity, available, condition);
        equipmentRepository.save(equipment);
    }

    public void deleteEquipment(String equipmentId) {
        if (equipmentRepository.findById(equipmentId).isEmpty()) {
            throw new ValidationException("Equipment not found: " + equipmentId);
        }
        equipmentRepository.delete(equipmentId);
    }

    public boolean isAvailable(String equipmentId, int requiredQuantity) {
        Optional<Equipment> equipmentOpt = equipmentRepository.findById(equipmentId);
        if (equipmentOpt.isEmpty()) {
            return false;
        }
        Equipment equipment = equipmentOpt.get();
        return equipment.getAvailable() >= requiredQuantity && 
               equipment.getCondition() == EquipmentCondition.GOOD;
    }

    public void reserveEquipment(String equipmentId, int quantity) {
        Optional<Equipment> equipmentOpt = equipmentRepository.findById(equipmentId);
        if (equipmentOpt.isEmpty()) {
            throw new ValidationException("Equipment not found: " + equipmentId);
        }
        Equipment equipment = equipmentOpt.get();
        if (equipment.getAvailable() < quantity) {
            throw new ValidationException("Not enough equipment available");
        }
        equipment.setAvailable(equipment.getAvailable() - quantity);
        equipmentRepository.save(equipment);
    }

    public void releaseEquipment(String equipmentId, int quantity) {
        Optional<Equipment> equipmentOpt = equipmentRepository.findById(equipmentId);
        if (equipmentOpt.isEmpty()) {
            return;
        }
        Equipment equipment = equipmentOpt.get();
        equipment.setAvailable(Math.min(equipment.getQuantity(), equipment.getAvailable() + quantity));
        equipmentRepository.save(equipment);
    }

    private void validateEquipment(String equipmentId, String name, String category, int quantity) {
        if (equipmentId == null || equipmentId.trim().isEmpty()) {
            throw new ValidationException("Equipment ID is required");
        }
        if (name == null || name.trim().isEmpty()) {
            throw new ValidationException("Equipment name is required");
        }
        if (category == null || category.trim().isEmpty()) {
            throw new ValidationException("Category is required");
        }
        if (quantity < 0) {
            throw new ValidationException("Quantity cannot be negative");
        }
    }
}

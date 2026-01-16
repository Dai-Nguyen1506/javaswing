package com.oop.project.repository.file;

import com.oop.project.model.Equipment;
import com.oop.project.repository.EquipmentRepository;
import com.oop.project.util.AppPaths;

import java.io.*;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

public class FileEquipmentRepository implements EquipmentRepository {
    private final Path filePath;

    public FileEquipmentRepository() {
        this(AppPaths.equipmentFile());
    }

    public FileEquipmentRepository(Path filePath) {
        this.filePath = filePath;
    }

    @Override
    public List<Equipment> findAll() {
        if (!Files.exists(filePath)) {
            return new ArrayList<>();
        }
        try (ObjectInputStream ois = new ObjectInputStream(new FileInputStream(filePath.toFile()))) {
            @SuppressWarnings("unchecked")
            List<Equipment> equipment = (List<Equipment>) ois.readObject();
            return equipment;
        } catch (EOFException e) {
            return new ArrayList<>();
        } catch (IOException | ClassNotFoundException e) {
            throw new IllegalStateException("Cannot read equipment file: " + filePath.toAbsolutePath(), e);
        }
    }

    @Override
    public Optional<Equipment> findById(String equipmentId) {
        return findAll().stream()
                .filter(e -> e.getEquipmentId().equalsIgnoreCase(equipmentId))
                .findFirst();
    }

    @Override
    public void save(Equipment equipment) {
        List<Equipment> equipmentList = findAll();
        equipmentList.removeIf(e -> e.getEquipmentId().equalsIgnoreCase(equipment.getEquipmentId()));
        equipmentList.add(equipment);
        saveAll(equipmentList);
    }

    @Override
    public void delete(String equipmentId) {
        List<Equipment> equipmentList = findAll();
        equipmentList.removeIf(e -> e.getEquipmentId().equalsIgnoreCase(equipmentId));
        saveAll(equipmentList);
    }

    @Override
    public List<Equipment> findByCategory(String category) {
        if (category == null || category.trim().isEmpty()) {
            return findAll();
        }
        return findAll().stream()
                .filter(e -> e.getCategory().equalsIgnoreCase(category.trim()))
                .collect(Collectors.toList());
    }

    private void saveAll(List<Equipment> equipmentList) {
        try (ObjectOutputStream oos = new ObjectOutputStream(new FileOutputStream(filePath.toFile()))) {
            oos.writeObject(equipmentList);
        } catch (IOException e) {
            throw new IllegalStateException("Cannot write equipment file: " + filePath.toAbsolutePath(), e);
        }
    }
}

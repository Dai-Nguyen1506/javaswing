package com.oop.project.model;

import java.io.Serializable;
import java.util.Objects;

public class Equipment implements Serializable {
    private static final long serialVersionUID = 1L;

    private String equipmentId;
    private String name;
    private String category;
    private int quantity;
    private int available;
    private EquipmentCondition condition;

    public Equipment() {
        this.condition = EquipmentCondition.GOOD;
    }

    public Equipment(String equipmentId, String name, String category, int quantity, int available, EquipmentCondition condition) {
        this.equipmentId = equipmentId;
        this.name = name;
        this.category = category;
        this.quantity = quantity;
        this.available = available;
        this.condition = condition != null ? condition : EquipmentCondition.GOOD;
    }

    public String getEquipmentId() {
        return equipmentId;
    }

    public void setEquipmentId(String equipmentId) {
        this.equipmentId = equipmentId;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getCategory() {
        return category;
    }

    public void setCategory(String category) {
        this.category = category;
    }

    public int getQuantity() {
        return quantity;
    }

    public void setQuantity(int quantity) {
        this.quantity = quantity;
    }

    public int getAvailable() {
        return available;
    }

    public void setAvailable(int available) {
        this.available = available;
    }

    public EquipmentCondition getCondition() {
        return condition;
    }

    public void setCondition(EquipmentCondition condition) {
        this.condition = condition;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Equipment equipment = (Equipment) o;
        return Objects.equals(equipmentId, equipment.equipmentId);
    }

    @Override
    public int hashCode() {
        return Objects.hash(equipmentId);
    }

    @Override
    public String toString() {
        return equipmentId + " - " + name;
    }
}

package com.oop.project.model;

import java.io.Serializable;

public class Item implements Serializable {
    private static final long serialVersionUID = 1L;

    private String itemId;
    private String itemName;
    private EquipmentCondition condition;
    private ItemStatus status;
    private String equipmentId;

    public Item() {
        this.condition = EquipmentCondition.GOOD;
        this.status = ItemStatus.AVAILABLE;
    }

    public Item(String itemId, String itemName, EquipmentCondition condition, ItemStatus status, String equipmentId) {
        this.itemId = itemId;
        this.itemName = itemName;
        this.condition = condition;
        this.status = status;
        this.equipmentId = equipmentId;
    }

    public String getItemId() {
        return itemId;
    }

    public void setItemId(String itemId) {
        this.itemId = itemId;
    }

    public String getItemName() {
        return itemName;
    }

    public void setItemName(String itemName) {
        this.itemName = itemName;
    }

    public EquipmentCondition getCondition() {
        return condition;
    }

    public void setCondition(EquipmentCondition condition) {
        this.condition = condition;
    }

    public ItemStatus getStatus() {
        return status;
    }

    public void setStatus(ItemStatus status) {
        this.status = status;
    }

    public String getEquipmentId() {
        return equipmentId;
    }

    public void setEquipmentId(String equipmentId) {
        this.equipmentId = equipmentId;
    }

    @Override
    public String toString() {
        return itemName + " (" + condition + ", " + status + ")";
    }
}

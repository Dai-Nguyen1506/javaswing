package com.oop.project.model;

import java.io.Serializable;
import java.time.LocalDateTime;
import java.util.Objects;

public class RentalContract implements Serializable {
    private static final long serialVersionUID = 1L;

    private String contractNumber;
    private String customerId;
    private String equipmentId;
    private LocalDateTime startTime;
    private int durationMinutes;
    private double rentalFee;
    private String lessonPackageId;
    private double lessonFee;
    private ContractStatus status;

    public RentalContract() {
        this.status = ContractStatus.DRAFT;
    }

    public RentalContract(String contractNumber, String customerId, String equipmentId,
                          LocalDateTime startTime, int durationMinutes, double rentalFee,
                          String lessonPackageId, double lessonFee, ContractStatus status) {
        this.contractNumber = contractNumber;
        this.customerId = customerId;
        this.equipmentId = equipmentId;
        this.startTime = startTime;
        this.durationMinutes = durationMinutes;
        this.rentalFee = rentalFee;
        this.lessonPackageId = lessonPackageId;
        this.lessonFee = lessonFee;
        this.status = status != null ? status : ContractStatus.DRAFT;
    }

    public String getContractNumber() {
        return contractNumber;
    }

    public void setContractNumber(String contractNumber) {
        this.contractNumber = contractNumber;
    }

    public String getCustomerId() {
        return customerId;
    }

    public void setCustomerId(String customerId) {
        this.customerId = customerId;
    }

    public String getEquipmentId() {
        return equipmentId;
    }

    public void setEquipmentId(String equipmentId) {
        this.equipmentId = equipmentId;
    }

    public LocalDateTime getStartTime() {
        return startTime;
    }

    public void setStartTime(LocalDateTime startTime) {
        this.startTime = startTime;
    }

    public int getDurationMinutes() {
        return durationMinutes;
    }

    public void setDurationMinutes(int durationMinutes) {
        this.durationMinutes = durationMinutes;
    }

    public double getRentalFee() {
        return rentalFee;
    }

    public void setRentalFee(double rentalFee) {
        this.rentalFee = rentalFee;
    }

    public String getLessonPackageId() {
        return lessonPackageId;
    }

    public void setLessonPackageId(String lessonPackageId) {
        this.lessonPackageId = lessonPackageId;
    }

    public double getLessonFee() {
        return lessonFee;
    }

    public void setLessonFee(double lessonFee) {
        this.lessonFee = lessonFee;
    }

    public ContractStatus getStatus() {
        return status;
    }

    public void setStatus(ContractStatus status) {
        this.status = status;
    }

    public double getTotalFee() {
        return rentalFee + lessonFee;
    }

    public LocalDateTime getEndTime() {
        return startTime != null ? startTime.plusMinutes(durationMinutes) : null;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        RentalContract that = (RentalContract) o;
        return Objects.equals(contractNumber, that.contractNumber);
    }

    @Override
    public int hashCode() {
        return Objects.hash(contractNumber);
    }

    @Override
    public String toString() {
        return contractNumber + " - " + customerId;
    }
}

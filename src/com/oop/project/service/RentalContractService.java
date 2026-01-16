package com.oop.project.service;

import com.oop.project.exception.ValidationException;
import com.oop.project.model.ContractStatus;
import com.oop.project.model.LessonPackage;
import com.oop.project.model.RentalContract;
import com.oop.project.repository.RentalContractRepository;
import com.oop.project.util.Validator;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

public class RentalContractService {
    private final RentalContractRepository rentalContractRepository;
    private final EquipmentService equipmentService;
    private final LessonPackageService lessonPackageService;

    public RentalContractService(RentalContractRepository rentalContractRepository,
                                 EquipmentService equipmentService,
                                 LessonPackageService lessonPackageService) {
        this.rentalContractRepository = rentalContractRepository;
        this.equipmentService = equipmentService;
        this.lessonPackageService = lessonPackageService;
    }

    public List<RentalContract> getAllContracts() {
        return rentalContractRepository.findAll();
    }

    public Optional<RentalContract> getContractByNumber(String contractNumber) {
        return rentalContractRepository.findByContractNumber(contractNumber);
    }

    public List<RentalContract> getContractsByStatus(ContractStatus status) {
        return rentalContractRepository.findByStatus(status);
    }

    public List<RentalContract> getContractsByCustomer(String customerId) {
        return rentalContractRepository.findByCustomerId(customerId);
    }

    public void createContract(String contractNumber, String customerId, String equipmentId,
                               LocalDateTime startTime, int durationMinutes, String lessonPackageId) {
        validateContract(contractNumber, customerId, equipmentId, startTime, durationMinutes);

        if (rentalContractRepository.findByContractNumber(contractNumber).isPresent()) {
            throw new ValidationException("Contract number already exists: " + contractNumber);
        }

        if (!equipmentService.isAvailable(equipmentId, 1)) {
            throw new ValidationException("Equipment is not available: " + equipmentId);
        }

        double rentalFee = calculateRentalFee(durationMinutes);
        double lessonFee = 0.0;

        if (lessonPackageId != null && !lessonPackageId.trim().isEmpty()) {
            Optional<LessonPackage> packageOpt = lessonPackageService.getPackageById(lessonPackageId);
            if (packageOpt.isEmpty()) {
                throw new ValidationException("Lesson package not found: " + lessonPackageId);
            }
            lessonFee = packageOpt.get().getPrice();
        }

        RentalContract contract = new RentalContract(contractNumber, customerId, equipmentId,
                startTime, durationMinutes, rentalFee, lessonPackageId, lessonFee, ContractStatus.DRAFT);

        rentalContractRepository.save(contract);
    }

    public void updateContractStatus(String contractNumber, ContractStatus newStatus) {
        Optional<RentalContract> contractOpt = rentalContractRepository.findByContractNumber(contractNumber);
        if (contractOpt.isEmpty()) {
            throw new ValidationException("Contract not found: " + contractNumber);
        }

        RentalContract contract = contractOpt.get();
        ContractStatus oldStatus = contract.getStatus();

        // Reserve equipment when activating
        if (oldStatus != ContractStatus.ACTIVE && newStatus == ContractStatus.ACTIVE) {
            equipmentService.reserveEquipment(contract.getEquipmentId(), 1);
        }

        // Release equipment when completing/returning
        if (oldStatus == ContractStatus.ACTIVE && 
            (newStatus == ContractStatus.COMPLETED || newStatus == ContractStatus.RETURNED)) {
            equipmentService.releaseEquipment(contract.getEquipmentId(), 1);
        }

        contract.setStatus(newStatus);
        rentalContractRepository.save(contract);
    }

    public void deleteContract(String contractNumber) {
        Optional<RentalContract> contractOpt = rentalContractRepository.findByContractNumber(contractNumber);
        if (contractOpt.isEmpty()) {
            throw new ValidationException("Contract not found: " + contractNumber);
        }

        RentalContract contract = contractOpt.get();
        
        // Release equipment if it was reserved
        if (contract.getStatus() == ContractStatus.ACTIVE) {
            equipmentService.releaseEquipment(contract.getEquipmentId(), 1);
        }

        rentalContractRepository.delete(contractNumber);
    }

    public void checkAndUpdateOverdueContracts() {
        LocalDateTime now = LocalDateTime.now();
        List<RentalContract> activeContracts = rentalContractRepository.findByStatus(ContractStatus.ACTIVE);

        for (RentalContract contract : activeContracts) {
            LocalDateTime endTime = contract.getEndTime();
            if (endTime != null && now.isAfter(endTime)) {
                contract.setStatus(ContractStatus.OVERDUE);
                rentalContractRepository.save(contract);
            }
        }
    }

    /**
     * Calculate rental fee: $40 per full hour + $1 per extra minute (capped at 40 minutes)
     */
    public double calculateRentalFee(int durationMinutes) {
        if (durationMinutes <= 0) {
            return 0.0;
        }

        int fullHours = durationMinutes / 60;
        int extraMinutes = durationMinutes % 60;

        // Cap extra minutes at 40
        if (extraMinutes > 40) {
            extraMinutes = 40;
        }

        return (fullHours * 40.0) + (extraMinutes * 1.0);
    }

    private void validateContract(String contractNumber, String customerId, String equipmentId,
                                  LocalDateTime startTime, int durationMinutes) {
        if (!Validator.isValidContractNumber(contractNumber)) {
            throw new ValidationException("Invalid contract number format. Must be one uppercase letter followed by three digits (e.g., A001)");
        }
        if (customerId == null || customerId.trim().isEmpty()) {
            throw new ValidationException("Customer ID is required");
        }
        if (equipmentId == null || equipmentId.trim().isEmpty()) {
            throw new ValidationException("Equipment ID is required");
        }
        if (startTime == null) {
            throw new ValidationException("Start time is required");
        }
        if (!Validator.isValidDuration(durationMinutes)) {
            throw new ValidationException("Duration must be between 60 and 7200 minutes (1 to 120 hours)");
        }
    }
}

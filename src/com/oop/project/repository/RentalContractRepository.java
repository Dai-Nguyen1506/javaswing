package com.oop.project.repository;

import com.oop.project.model.ContractStatus;
import com.oop.project.model.RentalContract;

import java.util.List;
import java.util.Optional;

public interface RentalContractRepository {
    List<RentalContract> findAll();
    Optional<RentalContract> findByContractNumber(String contractNumber);
    void save(RentalContract contract);
    void delete(String contractNumber);
    List<RentalContract> findByStatus(ContractStatus status);
    List<RentalContract> findByCustomerId(String customerId);
}

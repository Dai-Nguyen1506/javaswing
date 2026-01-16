package com.oop.project.repository.file;

import com.oop.project.model.ContractStatus;
import com.oop.project.model.RentalContract;
import com.oop.project.repository.RentalContractRepository;
import com.oop.project.util.AppPaths;

import java.io.*;
import java.nio.file.Files;
import java.nio.file.Path;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

/**
 * Random Access File implementation for RentalContract storage.
 * Each contract is stored at a fixed position based on its contract number.
 * Contract number format: [A-Z][0-9]{3} (e.g., A001, B123)
 */
public class FileRentalContractRepository implements RentalContractRepository {
    private static final int RECORD_SIZE = 512; // Fixed size per record
    private final Path filePath;

    public FileRentalContractRepository() {
        this(AppPaths.rentalContractsFile());
    }

    public FileRentalContractRepository(Path filePath) {
        this.filePath = filePath;
    }

    @Override
    public Optional<RentalContract> findByContractNumber(String contractNumber) {
        if (contractNumber == null || contractNumber.trim().isEmpty()) {
            return Optional.empty();
        }
        
        int position = calculatePosition(contractNumber.trim().toUpperCase());
        
        try (RandomAccessFile raf = new RandomAccessFile(filePath.toFile(), "r")) {
            if (raf.length() < (long)(position + 1) * RECORD_SIZE) {
                return Optional.empty();
            }
            
            raf.seek((long)position * RECORD_SIZE);
            String storedNumber = readString(raf, 10);
            
            if (storedNumber.isEmpty() || !storedNumber.equals(contractNumber.trim().toUpperCase())) {
                return Optional.empty();
            }
            
            RentalContract contract = readContract(raf, storedNumber);
            return Optional.of(contract);
        } catch (FileNotFoundException e) {
            return Optional.empty();
        } catch (IOException e) {
            throw new IllegalStateException("Cannot read contract: " + contractNumber, e);
        }
    }

    @Override
    public void save(RentalContract contract) {
        String contractNumber = contract.getContractNumber().trim().toUpperCase();
        int position = calculatePosition(contractNumber);
        
        try (RandomAccessFile raf = new RandomAccessFile(filePath.toFile(), "rw")) {
            raf.seek((long)position * RECORD_SIZE);
            writeContract(raf, contract);
        } catch (IOException e) {
            throw new IllegalStateException("Cannot save contract: " + contractNumber, e);
        }
    }

    @Override
    public void delete(String contractNumber) {
        int position = calculatePosition(contractNumber.trim().toUpperCase());
        
        try (RandomAccessFile raf = new RandomAccessFile(filePath.toFile(), "rw")) {
            if (raf.length() >= (long)(position + 1) * RECORD_SIZE) {
                raf.seek((long)position * RECORD_SIZE);
                // Write empty marker
                writeString(raf, "", 10);
            }
        } catch (FileNotFoundException e) {
            // File doesn't exist, nothing to delete
        } catch (IOException e) {
            throw new IllegalStateException("Cannot delete contract: " + contractNumber, e);
        }
    }

    @Override
    public List<RentalContract> findAll() {
        List<RentalContract> contracts = new ArrayList<>();
        
        if (!Files.exists(filePath)) {
            return contracts;
        }
        
        try (RandomAccessFile raf = new RandomAccessFile(filePath.toFile(), "r")) {
            long fileLength = raf.length();
            int recordCount = (int)(fileLength / RECORD_SIZE);
            
            for (int i = 0; i < recordCount; i++) {
                raf.seek((long)i * RECORD_SIZE);
                String contractNumber = readString(raf, 10);
                
                if (!contractNumber.isEmpty()) {
                    RentalContract contract = readContract(raf, contractNumber);
                    contracts.add(contract);
                }
            }
        } catch (FileNotFoundException e) {
            return contracts;
        } catch (IOException e) {
            throw new IllegalStateException("Cannot read contracts file", e);
        }
        
        return contracts;
    }

    @Override
    public List<RentalContract> findByStatus(ContractStatus status) {
        return findAll().stream()
                .filter(c -> c.getStatus() == status)
                .collect(Collectors.toList());
    }

    @Override
    public List<RentalContract> findByCustomerId(String customerId) {
        if (customerId == null || customerId.trim().isEmpty()) {
            return new ArrayList<>();
        }
        return findAll().stream()
                .filter(c -> c.getCustomerId().equalsIgnoreCase(customerId.trim()))
                .collect(Collectors.toList());
    }

    private int calculatePosition(String contractNumber) {
        // Contract format: [A-Z][0-9]{3}
        // Position = (letter - 'A') * 1000 + number
        char letter = contractNumber.charAt(0);
        int number = Integer.parseInt(contractNumber.substring(1));
        return (letter - 'A') * 1000 + number;
    }

    private void writeContract(RandomAccessFile raf, RentalContract contract) throws IOException {
        writeString(raf, contract.getContractNumber(), 10);
        writeString(raf, contract.getCustomerId(), 20);
        writeString(raf, contract.getEquipmentId(), 20);
        writeString(raf, contract.getStartTime().toString(), 30);
        raf.writeInt(contract.getDurationMinutes());
        raf.writeDouble(contract.getRentalFee());
        writeString(raf, contract.getLessonPackageId() != null ? contract.getLessonPackageId() : "", 20);
        raf.writeDouble(contract.getLessonFee());
        writeString(raf, contract.getStatus().name(), 20);
    }

    private RentalContract readContract(RandomAccessFile raf, String contractNumber) throws IOException {
        String customerId = readString(raf, 20);
        String equipmentId = readString(raf, 20);
        String startTimeStr = readString(raf, 30);
        int durationMinutes = raf.readInt();
        double rentalFee = raf.readDouble();
        String lessonPackageId = readString(raf, 20);
        double lessonFee = raf.readDouble();
        String statusStr = readString(raf, 20);
        
        LocalDateTime startTime = LocalDateTime.parse(startTimeStr);
        ContractStatus status = ContractStatus.fromString(statusStr);
        
        return new RentalContract(contractNumber, customerId, equipmentId, startTime, durationMinutes,
                rentalFee, lessonPackageId.isEmpty() ? null : lessonPackageId, lessonFee, status);
    }

    private void writeString(RandomAccessFile raf, String str, int maxLength) throws IOException {
        String trimmed = str != null ? str : "";
        if (trimmed.length() > maxLength) {
            trimmed = trimmed.substring(0, maxLength);
        }
        byte[] bytes = new byte[maxLength];
        byte[] strBytes = trimmed.getBytes("UTF-8");
        System.arraycopy(strBytes, 0, bytes, 0, Math.min(strBytes.length, maxLength));
        raf.write(bytes);
    }

    private String readString(RandomAccessFile raf, int length) throws IOException {
        byte[] bytes = new byte[length];
        raf.readFully(bytes);
        return new String(bytes, "UTF-8").trim().replace("\0", "");
    }
}

package com.oop.project.repository.file;

import com.oop.project.model.Customer;
import com.oop.project.repository.CustomerRepository;
import com.oop.project.util.AppPaths;

import java.io.*;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

public class FileCustomerRepository implements CustomerRepository {
    private final Path filePath;

    public FileCustomerRepository() {
        this(AppPaths.customersFile());
    }

    public FileCustomerRepository(Path filePath) {
        this.filePath = filePath;
    }

    @Override
    public List<Customer> findAll() {
        if (!Files.exists(filePath)) {
            return new ArrayList<>();
        }
        try (ObjectInputStream ois = new ObjectInputStream(new FileInputStream(filePath.toFile()))) {
            @SuppressWarnings("unchecked")
            List<Customer> customers = (List<Customer>) ois.readObject();
            return customers;
        } catch (EOFException e) {
            return new ArrayList<>();
        } catch (IOException | ClassNotFoundException e) {
            throw new IllegalStateException("Cannot read customers file: " + filePath.toAbsolutePath(), e);
        }
    }

    @Override
    public Optional<Customer> findById(String customerId) {
        return findAll().stream()
                .filter(c -> c.getCustomerId().equalsIgnoreCase(customerId))
                .findFirst();
    }

    @Override
    public void save(Customer customer) {
        List<Customer> customers = findAll();
        customers.removeIf(c -> c.getCustomerId().equalsIgnoreCase(customer.getCustomerId()));
        customers.add(customer);
        saveAll(customers);
    }

    @Override
    public void delete(String customerId) {
        List<Customer> customers = findAll();
        customers.removeIf(c -> c.getCustomerId().equalsIgnoreCase(customerId));
        saveAll(customers);
    }

    @Override
    public List<Customer> search(String keyword) {
        if (keyword == null || keyword.trim().isEmpty()) {
            return findAll();
        }
        String kw = keyword.trim().toLowerCase();
        return findAll().stream()
                .filter(c -> c.getName().toLowerCase().contains(kw) ||
                             c.getPhone().toLowerCase().contains(kw) ||
                             (c.getEmail() != null && c.getEmail().toLowerCase().contains(kw)))
                .collect(Collectors.toList());
    }

    private void saveAll(List<Customer> customers) {
        try (ObjectOutputStream oos = new ObjectOutputStream(new FileOutputStream(filePath.toFile()))) {
            oos.writeObject(customers);
        } catch (IOException e) {
            throw new IllegalStateException("Cannot write customers file: " + filePath.toAbsolutePath(), e);
        }
    }
}

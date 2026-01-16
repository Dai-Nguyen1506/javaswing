package com.oop.project.repository;

import com.oop.project.model.Customer;

import java.util.List;
import java.util.Optional;

public interface CustomerRepository {
    List<Customer> findAll();
    Optional<Customer> findById(String customerId);
    void save(Customer customer);
    void delete(String customerId);
    List<Customer> search(String keyword);
}

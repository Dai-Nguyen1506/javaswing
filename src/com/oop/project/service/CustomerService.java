package com.oop.project.service;

import com.oop.project.exception.ValidationException;
import com.oop.project.model.Customer;
import com.oop.project.repository.CustomerRepository;
import com.oop.project.util.Validator;

import java.util.List;
import java.util.Optional;

public class CustomerService {
    private final CustomerRepository customerRepository;

    public CustomerService(CustomerRepository customerRepository) {
        this.customerRepository = customerRepository;
    }

    public List<Customer> getAllCustomers() {
        return customerRepository.findAll();
    }

    public Optional<Customer> getCustomerById(String customerId) {
        return customerRepository.findById(customerId);
    }

    public void createCustomer(String customerId, String name, String phone, String email, String address) {
        validateCustomer(customerId, name, phone, email);
        
        if (customerRepository.findById(customerId).isPresent()) {
            throw new ValidationException("Customer ID already exists: " + customerId);
        }

        Customer customer = new Customer(customerId, name, phone, email, address);
        customerRepository.save(customer);
    }

    public void updateCustomer(String customerId, String name, String phone, String email, String address) {
        validateCustomer(customerId, name, phone, email);

        if (customerRepository.findById(customerId).isEmpty()) {
            throw new ValidationException("Customer not found: " + customerId);
        }

        Customer customer = new Customer(customerId, name, phone, email, address);
        customerRepository.save(customer);
    }

    public void deleteCustomer(String customerId) {
        if (customerRepository.findById(customerId).isEmpty()) {
            throw new ValidationException("Customer not found: " + customerId);
        }
        customerRepository.delete(customerId);
    }

    public List<Customer> searchCustomers(String keyword) {
        return customerRepository.search(keyword);
    }

    private void validateCustomer(String customerId, String name, String phone, String email) {
        if (customerId == null || customerId.trim().isEmpty()) {
            throw new ValidationException("Customer ID is required");
        }
        if (name == null || name.trim().isEmpty()) {
            throw new ValidationException("Customer name is required");
        }
        if (!Validator.isValidPhone(phone)) {
            throw new ValidationException("Invalid phone number format. Must be 10-15 digits.");
        }
        if (email != null && !email.trim().isEmpty() && !Validator.isValidEmail(email)) {
            throw new ValidationException("Invalid email format");
        }
    }
}

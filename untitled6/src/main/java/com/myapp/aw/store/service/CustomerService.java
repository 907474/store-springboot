package com.myapp.aw.store.service;

import com.myapp.aw.store.model.Customer;
import com.myapp.aw.store.model.UserStatus;
import com.myapp.aw.store.repository.CustomerRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

@Service
public class CustomerService {

    private final CustomerRepository customerRepository;
    private final PasswordEncoder passwordEncoder;

    @Autowired
    public CustomerService(CustomerRepository customerRepository, PasswordEncoder passwordEncoder) {
        this.customerRepository = customerRepository;
        this.passwordEncoder = passwordEncoder;
    }

    public Customer registerCustomer(String username, String rawPassword) {
        Customer newCustomer = new Customer();
        newCustomer.setUsername(username);
        newCustomer.setPassword(passwordEncoder.encode(rawPassword));
        newCustomer.setRole("ROLE_CUSTOMER");
        newCustomer.setStatus(UserStatus.ENABLED); // Set default status

        return customerRepository.save(newCustomer);
    }

    public boolean checkLogin(String username, String rawPassword) {
        return customerRepository.findByUsername(username)
                .map(customer -> passwordEncoder.matches(rawPassword, customer.getPassword()))
                .orElse(false);
    }
}
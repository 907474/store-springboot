package com.myapp.aw.store.service;

import com.myapp.aw.store.model.AdminUser;
import com.myapp.aw.store.model.UserStatus;
import com.myapp.aw.store.repository.AdminUserRepository;
import com.myapp.aw.store.repository.CustomerRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;

@Service
public class AdminUserService {

    private static final Logger log = LoggerFactory.getLogger(AdminUserService.class);

    private final AdminUserRepository adminUserRepository;
    private final CustomerRepository customerRepository;
    private final PasswordEncoder passwordEncoder;

    @Autowired
    public AdminUserService(AdminUserRepository adminUserRepository, CustomerRepository customerRepository, PasswordEncoder passwordEncoder) {
        this.adminUserRepository = adminUserRepository;
        this.customerRepository = customerRepository;
        this.passwordEncoder = passwordEncoder;
    }

    public AdminUser createAdmin(String username, String rawPassword, String role) {
        log.info("Attempting to create a new admin user with username: {}", username);
        if (adminUserRepository.findByUsername(username).isPresent() || customerRepository.findByUsername(username).isPresent()) {
            log.warn("Admin creation failed: Username '{}' already exists.", username);
            throw new IllegalStateException("Username '" + username + "' already exists.");
        }
        AdminUser newAdmin = new AdminUser();
        newAdmin.setUsername(username);
        newAdmin.setPassword(passwordEncoder.encode(rawPassword));
        newAdmin.setRole(role);
        newAdmin.setStatus(UserStatus.ENABLED);
        AdminUser savedAdmin = adminUserRepository.save(newAdmin);
        log.info("Successfully created new admin user with ID: {}", savedAdmin.getId());
        return savedAdmin;
    }

    public void disableAdmin(Long adminId) {
        log.info("Attempting to disable admin user with ID: {}", adminId);
        AdminUser admin = adminUserRepository.findById(adminId)
                .orElseThrow(() -> {
                    log.error("Disable admin failed: Admin user with ID {} not found.", adminId);
                    return new ResponseStatusException(HttpStatus.NOT_FOUND, "Admin user not found");
                });

        admin.setStatus(UserStatus.DISABLED);
        adminUserRepository.save(admin);
        log.info("Successfully disabled admin user with ID: {}", adminId);
    }
}
package com.myapp.aw.store.service;

import com.myapp.aw.store.model.AdminUser;
import com.myapp.aw.store.model.Customer;
import com.myapp.aw.store.model.UserStatus;
import com.myapp.aw.store.repository.AdminUserRepository;
import com.myapp.aw.store.repository.CustomerRepository;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

import java.util.Collections;
import java.util.Optional;

@Service
public class CustomUserDetailsService implements UserDetailsService {

    private final CustomerRepository customerRepository;
    private final AdminUserRepository adminUserRepository;

    public CustomUserDetailsService(CustomerRepository customerRepository, AdminUserRepository adminUserRepository) {
        this.customerRepository = customerRepository;
        this.adminUserRepository = adminUserRepository;
    }

    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        Optional<Customer> customerOpt = customerRepository.findByUsername(username);
        if (customerOpt.isPresent()) {
            Customer customer = customerOpt.get();
            GrantedAuthority authority = new SimpleGrantedAuthority(customer.getRole());
            boolean isEnabled = customer.getStatus() == UserStatus.ENABLED;
            return new User(customer.getUsername(), customer.getPassword(), isEnabled, true, true, true, Collections.singletonList(authority));
        }

        Optional<AdminUser> adminOpt = adminUserRepository.findByUsername(username);
        if (adminOpt.isPresent()) {
            AdminUser admin = adminOpt.get();
            GrantedAuthority authority = new SimpleGrantedAuthority(admin.getRole());
            boolean isEnabled = admin.getStatus() == UserStatus.ENABLED;
            return new User(admin.getUsername(), admin.getPassword(), isEnabled, true, true, true, Collections.singletonList(authority));
        }

        throw new UsernameNotFoundException("User not found with username: " + username);
    }
}
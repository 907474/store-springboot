package com.myapp.aw.store.controller;

import com.myapp.aw.store.model.Customer;
import com.myapp.aw.store.model.OrderArchive;
import com.myapp.aw.store.repository.CustomerRepository;
import com.myapp.aw.store.repository.OrderArchiveRepository;
import org.springframework.http.HttpStatus;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.server.ResponseStatusException;

import java.util.List;
import java.util.stream.Collectors;

@Controller
public class AccountController {

    private final CustomerRepository customerRepository;
    private final OrderArchiveRepository orderArchiveRepository;

    public AccountController(CustomerRepository customerRepository, OrderArchiveRepository orderArchiveRepository) {
        this.customerRepository = customerRepository;
        this.orderArchiveRepository = orderArchiveRepository;
    }

    @GetMapping("/account")
    public String viewAccountPage(Authentication authentication, Model model) {
        String username = authentication.getName();
        model.addAttribute("username", username);
        boolean isAdmin = authentication.getAuthorities().stream()
                .anyMatch(auth -> auth.getAuthority().startsWith("ROLE_ADMIN"));

        if (isAdmin) {
            String roles = authentication.getAuthorities().stream()
                    .map(GrantedAuthority::getAuthority)
                    .collect(Collectors.joining(", "));
            model.addAttribute("roles", roles);
            return "admin-account";
        } else {
            Customer customer = customerRepository.findByUsername(username)
                    .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Customer not found"));

            List<OrderArchive> pastOrders = orderArchiveRepository.findByCustomerId(customer.getId());

            model.addAttribute("pastOrders", pastOrders);

            return "customer-account";
        }
    }
}
package com.myapp.aw.store.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;

@Configuration
public class SecurityConfig {

    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }

    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
        http
                .authorizeHttpRequests(auth -> auth
                        // T3-only pages
                        .requestMatchers("/admin/create-user", "/admin/users", "/admin/disable", "/admin/customers").hasRole("ADMIN_T3")
                        // T2 and T3 pages
                        .requestMatchers("/admin/edit-products", "/admin/edit-product", "/admin/search-products", "/admin/add-product", "/admin/bulk-edit", "/admin/products/download", "/admin/products/upload").hasAnyRole("ADMIN_T2", "ADMIN_T3")
                        // General admin pages
                        .requestMatchers("/admin/**").hasAnyRole("ADMIN_T1", "ADMIN_T2", "ADMIN_T3")
                        // Public pages
                        .requestMatchers(
                                "/", "/error", "/login", "/register", "/cart",
                                "/cart/add", "/cart/associate", "/cart/create",
                                "/cart/update", "/cart/remove", "/checkout",
                                "/order/place", "/order/confirmed",
                                "/product/**", "/search-products", "/css/**", "/js/**"
                        ).permitAll().anyRequest().authenticated()
                )
                .formLogin(form -> form
                        .loginPage("/login")
                        .permitAll()
                )
                .logout(logout -> logout
                        .logoutSuccessUrl("/")
                        .permitAll()
                );
        return http.build();
    }
}
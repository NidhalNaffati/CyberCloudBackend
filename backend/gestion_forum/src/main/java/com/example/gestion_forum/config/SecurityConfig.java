package com.example.gestion_forum.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;

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
                        .requestMatchers("/**").permitAll() // ✅ Fully public API (NO AUTH REQUIRED)
                )
                .csrf(csrf -> csrf.disable()) // ✅ Disable CSRF to prevent `403 Forbidden`
                .formLogin(form -> form.disable()) // ✅ Disable login form
                .httpBasic(httpBasic -> httpBasic.disable()); // ✅ Disable Basic Auth

        return http.build();
    }
}

package tn.esprit.config;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.ApplicationArguments;
import org.springframework.boot.ApplicationRunner;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;
import tn.esprit.entity.User;
import tn.esprit.repository.UserRepository;

import java.util.function.Supplier;

import static tn.esprit.entity.Role.ROLE_ADMIN;
import static tn.esprit.entity.Role.ROLE_USER;


@Slf4j
@Component
@RequiredArgsConstructor
public class UserInitializer implements ApplicationRunner {

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;

    @Value("${admin.username}")
    private String adminUsername;

    @Value("${admin.password}")
    private String adminPassword;

    private final String USERNAME = "user@mail.com";

    private final String USERPASSWORD = "password123";

    private User buildAdminUser() {
        return User.builder()
            .firstName("admin")
            .lastName("admin")
            .email(adminUsername)
            .password(adminPassword)
            .confirmPassword(adminPassword)
            .role(ROLE_ADMIN)
            .enabled(true)
            .accountNonLocked(true)
            .build();
    }

    private User buildRegularUser() {
        return User.builder()
            .firstName("User")
            .lastName("Demo")
            .email(USERNAME)
            .password(USERPASSWORD)
            .confirmPassword(USERPASSWORD)
            .role(ROLE_USER)
            .enabled(true)
            .accountNonLocked(true)
            .build();
    }

    @Override
    public void run(ApplicationArguments args) {
        initializeUser(adminUsername, this::buildAdminUser, "Admin");
        initializeUser(USERNAME, this::buildRegularUser, "Regular user");
    }

    private void initializeUser(String username, Supplier<User> userSupplier, String userType) {
        if (userRepository.existsByEmail(username)) {
            log.info("{} user already exists. username: {}", userType, username);
            return;
        }

        User user = userSupplier.get();
        user.setPassword(passwordEncoder.encode(user.getPassword()));
        userRepository.save(user);

        log.info("{} user created successfully. username: {}", userType, username);
    }
}
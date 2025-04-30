// AdminUserBuilder class
package tn.esprit.config;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.ApplicationArguments;
import org.springframework.boot.ApplicationRunner;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;
import tn.esprit.entity.User;
import tn.esprit.repository.UserRepository;

import static tn.esprit.entity.Role.ROLE_ADMIN;

@Slf4j
@Component
public class AdminUserBuilder extends UserBuilder implements ApplicationRunner {

    @Value("${admin.username}")
    private String adminUsername;

    @Value("${admin.password}")
    private String adminPassword;

    public AdminUserBuilder(UserRepository userRepository, PasswordEncoder passwordEncoder) {
        super(userRepository, passwordEncoder);
    }

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
            .documentsVerified(true)
            .build();
    }

    @Override
    public void run(ApplicationArguments args) {
        initializeUser(adminUsername, this::buildAdminUser, "Admin");
    }
}
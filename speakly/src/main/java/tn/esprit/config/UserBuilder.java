// Base User Builder class
package tn.esprit.config;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.crypto.password.PasswordEncoder;
import tn.esprit.entity.User;
import tn.esprit.repository.UserRepository;

import java.util.function.Supplier;

@Slf4j
@RequiredArgsConstructor
public abstract class UserBuilder {
    protected final UserRepository userRepository;
    protected final PasswordEncoder passwordEncoder;

    protected void initializeUser(String username, Supplier<User> userSupplier, String userType) {
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
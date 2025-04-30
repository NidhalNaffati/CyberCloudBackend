package tn.esprit.config;

import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.ApplicationArguments;
import org.springframework.boot.ApplicationRunner;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;
import tn.esprit.entity.User;
import tn.esprit.repository.UserRepository;

import java.util.Arrays;
import java.util.List;

import static tn.esprit.entity.Role.ROLE_USER;

@Slf4j
@Component
public class RegularUserBuilder extends UserBuilder implements ApplicationRunner {

    private final PasswordEncoder passwordEncoder;

    public RegularUserBuilder(UserRepository userRepository, PasswordEncoder passwordEncoder) {
        super(userRepository, passwordEncoder);
        this.passwordEncoder = passwordEncoder;
    }

    private List<User> buildRegularUsers() {
        return Arrays.asList(
            createUser("pierreL87", "pierre.leclerc@example.com", "LyonDoc25", "Pierre", "Leclerc"),
            createUser("mabroukighazi", "mabroukighazi2020@gmail.com", "Ghazi12@", "Mabrouki", "Ghazi"),
            createUser("fatmaA95", "fatma.aloui@example.com", "SfaxDoc2025", "Fatma", "Aloui"),
            createUser("Ghazi", "mabroukighazi@gmail.com", "Ghazi12@", "Ghazi", "Unknown"),
            createUser("dHW7wyqfvXwj0cSWfcJP-_YvRPPFZx", "mail@mail.com", "testMs12@", "Unknown", "User"),
            createUser("drJohn78", "john.smith@example.com", "LondonMD25", "John", "Smith"),
            createUser("drEmily80", "emily.brown@example.com", "UKHealth25", "Emily", "Brown"),
            createUser("nourEH93", "nour.elhouda@example.com", "TunisCare25", "Nour", "Elhouda"),
            createUser("ADMIN", "ghazi.mabrouki@esprit.tn", "Ghazi12@", "Admin", "Unknown"),
            createUser("claireM88", "claire.martin@example.com", "FranceDoc25", "Claire", "Martin"),
            createUser("aminaT92", "amina.trabelsi@example.com", "TunisOrtho25", "Amina", "Trabelsi"),
            createUser("Rezgui", "maram@gmail.com", "maram123@", "Maram", "Rezgui"),
            createUser("fedi", "Fadi.Tarkhani@esprit.tn", "MJ8sSbUSW5zf2xP", "Fadi", "Tarkhani"),
            createUser("nounou", "noorfarhat45@gmail.com", "nour1010", "Noor", "Farhat")
        );
    }

    private User createUser(String username, String email, String password, String firstName, String lastName) {
        return User.builder()
            .firstName(firstName)
            .lastName(lastName)
            .email(email)
            .email(username)
            .password(passwordEncoder.encode(password))
            .confirmPassword(passwordEncoder.encode(password))
            .role(ROLE_USER)
            .enabled(true)
            .accountNonLocked(true)
            .documentsVerified(false)
            .build();
    }

    @Override
    public void run(ApplicationArguments args) {
        List<User> users = buildRegularUsers();
        for (int i = 0; i < users.size(); i++) {
            User user = users.get(i);
            initializeUser(user.getEmail(), () -> user, "Regular user " + (i + 1));
        }
    }
}
package tn.esprit.requests;


import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotNull;
import org.hibernate.validator.constraints.Length;
import tn.esprit.entity.Role;
import tn.esprit.entity.User;


public record RegisterRequest(
    @Length(min = 3, max = 16, message = "first name length should be less than 16 and more than 3 ")
    String firstName,

    @Length(min = 3, max = 16, message = "last name length should be less than 16 and more than 3 ")
    String lastName,

    @Email(message = "Email should be valid")
    @NotNull(message = "Email shouldn't be null")
    @Length(min = 3, message = "email length should be more than 10 ")
    String email,

    @NotNull(message = "Password shouldn't be null ")
    @Length(min = 8, max = 16, message = "password length should be more than 8 and less than 16")
    String password,

    @NotNull
    String confirmPassword,

    Role role
) {
    public User toUser() {
        return User.builder()
            .firstName(firstName)
            .lastName(lastName)
            .email(email)
            .password(password)
            .confirmPassword(confirmPassword)
            .role(Role.ROLE_USER)
            .accountNonLocked(true)
            .enabled(false)
            .failedAttempts(0)
            .document(null)
            .documentsVerified(true)
            .build();
    }

    public User toMedecin() {
        return User.builder()
            .firstName(firstName)
            .lastName(lastName)
            .email(email)
            .password(password)
            .confirmPassword(confirmPassword)
            .role(Role.ROLE_MEDECIN)
            .accountNonLocked(true)
            .enabled(false)
            .failedAttempts(0)
            .documentsVerified(false)
            .build();
    }
}
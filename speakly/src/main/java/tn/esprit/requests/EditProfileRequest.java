package tn.esprit.requests;

import jakarta.validation.constraints.Email;
import org.hibernate.validator.constraints.Length;

public record EditProfileRequest(
    @Length(min = 3, max = 16, message = "First name length should be between 3 and 16 characters")
    String firstName,

    @Length(min = 3, max = 16, message = "Last name length should be between 3 and 16 characters")
    String lastName,

    @Email(message = "Email should be valid")
    @Length(min = 3, message = "Email length should be more than 3 characters")
    String email,

    String password,

    String confirmPassword
) {
}
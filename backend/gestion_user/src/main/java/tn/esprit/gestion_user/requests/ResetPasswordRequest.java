package tn.esprit.gestion_user.requests;

import lombok.Data;

@Data
public class ResetPasswordRequest {
        private String email;
        private String code;
        private String newPassword;
        private String confirmPassword;
}
package tn.esprit.gestion_user.requests;

import lombok.Data;

@Data
public class VerifyAccountRequest {
    private String email;
    private String code;
}

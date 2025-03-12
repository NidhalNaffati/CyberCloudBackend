package tn.esprit.gestion_user.requests;

public record AuthenticationRequest(
        String email,

        String password
) {

}

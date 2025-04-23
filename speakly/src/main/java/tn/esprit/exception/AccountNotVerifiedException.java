package tn.esprit.exception;

import org.springframework.http.HttpStatus;
import org.springframework.web.server.ResponseStatusException;

public class AccountNotVerifiedException extends ResponseStatusException {
    public AccountNotVerifiedException() {
        super(HttpStatus.BAD_REQUEST, "Your medical documents have not been verified yet. Please wait for admin approval.");
    }
}

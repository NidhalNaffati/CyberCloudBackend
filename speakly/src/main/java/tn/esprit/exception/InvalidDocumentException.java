package tn.esprit.exception;


import org.springframework.http.HttpStatus;
import org.springframework.web.server.ResponseStatusException;

public class InvalidDocumentException extends ResponseStatusException {
    public InvalidDocumentException() {
        super(HttpStatus.BAD_REQUEST, "The provided document is invalid or does not meet the required criteria.");
    }
}

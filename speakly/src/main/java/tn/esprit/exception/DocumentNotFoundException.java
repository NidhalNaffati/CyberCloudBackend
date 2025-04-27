package tn.esprit.exception;

import org.springframework.http.HttpStatus;
import org.springframework.web.server.ResponseStatusException;

public class DocumentNotFoundException extends ResponseStatusException {
    public DocumentNotFoundException() {
        super(HttpStatus.BAD_REQUEST, "Document not found");
    }
}
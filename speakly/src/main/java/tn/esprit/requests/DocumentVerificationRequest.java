package tn.esprit.requests;

public record DocumentVerificationRequest(
    String email,
    boolean isVerified
) {
}

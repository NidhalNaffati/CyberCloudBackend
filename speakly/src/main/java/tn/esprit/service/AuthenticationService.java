package tn.esprit.service;


import io.jsonwebtoken.ExpiredJwtException;
import io.jsonwebtoken.MalformedJwtException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.transaction.Transactional;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.mail.MailSendException;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;
import tn.esprit.entity.User;
import tn.esprit.entity.VerificationCode;
import tn.esprit.exception.EmailAlreadyExistsException;
import tn.esprit.exception.PasswordDontMatchException;
import tn.esprit.requests.AuthenticationRequest;
import tn.esprit.requests.AuthenticationResponse;
import tn.esprit.requests.RegisterRequest;

import java.io.IOException;

import static jakarta.servlet.http.HttpServletResponse.SC_UNAUTHORIZED;

/**
 * The AuthenticationService class is responsible for user authentication and registration processes.
 * <p>
 * It provides methods to authenticate users, register new users, send reset password requests, update passwords, enable user accounts, and refresh authentication tokens.
 */

@Slf4j
@Service
@AllArgsConstructor
public class AuthenticationService {

    private final AuthenticationManager authenticationManager;
    private final UserService userService;
    private final EmailService emailService;
    private final JwtService jwtService;
    private final TokenService tokenService;
    private final VerificationCodeService verificationCodeService;
    private final MedecinDocumentService medecinDocumentService;


    /**
     * Authenticates the user and generates a JWT token.
     *
     * @param request the authentication request containing email and password
     * @return the authentication response containing the JWT token
     * @throws BadCredentialsException if the credentials are invalid
     */
    public AuthenticationResponse authenticate(AuthenticationRequest request) {

        // If the authentication is successful, retrieves the user from the database and generates a JWT token
        User user = userService.validateCredentials(request.email(), request.password());
        log.info("User {} successfully authenticated with role {}", user.getFirstName(), user.getRole());


        // Attempts to authenticate the user with the provided email and password
        authenticationManager.authenticate(
            new UsernamePasswordAuthenticationToken(
                request.email(),
                request.password()
            )
        );


        String accessToken = jwtService.generateAccessToken(user);
        String refreshToken = jwtService.generateRefreshToken(user.getEmail());

        tokenService.revokeAllUserTokens(user);
        tokenService.saveUserToken(user, accessToken);

        // Returns an authentication response containing the JWT token
        return new AuthenticationResponse(accessToken, refreshToken);
    }

    @Transactional
    public void registerMedecinUser(RegisterRequest registerRequest, MultipartFile documentFile) {
        // If the password and password confirm do not match, throws an exception
        if (!isPasswordAndPasswordConfirmMatches(registerRequest)) {
            log.error("Password and password confirm doesn't match");
            throw new PasswordDontMatchException();
        }

        // If an account with the given email already exists, throws an exception
        if (userService.emailExists(registerRequest.email())) {
            log.error("Email already exists");
            throw new EmailAlreadyExistsException();
        }

        try {
            // Create the user without document initially
            User medecin = registerRequest.toMedecin();

            // Save the user first
            userService.saveUser(medecin);

            // Then upload the document file if provided
            if (documentFile != null && !documentFile.isEmpty()) {
                log.info("Uploading medecin documents for user {}", registerRequest.email());
                medecinDocumentService.uploadMedecinDocuments(registerRequest.email(), documentFile);
            } else {
                log.warn("No document file provided for medecin {}", registerRequest.email());
            }

            // Generate a verification code
            VerificationCode verificationCode = verificationCodeService.createVerificationCode(
                registerRequest.email(),
                VerificationCode.CodeType.ACCOUNT_ACTIVATION
            );

            // Send the verification code email
            log.info("Sending activation code {} to user {}", verificationCode.getCode(), registerRequest.email());

            emailService.sendActivationCode(
                registerRequest.email(),
                registerRequest.firstName(),
                verificationCode.getCode()
            );

            emailService.sendDocumentSubmissionWaitingApproval(
                registerRequest.email(),
                registerRequest.firstName()
            );

            log.info("Medecin successfully registered with request {}", registerRequest);
        } catch (Exception e) {
            log.error("Cannot create medecin user with request {}", registerRequest);
            log.error("Error: {}", e.getMessage());
            throw e;
        }
    }

    public void registerUser(RegisterRequest registerRequest) {
        // If the password and password confirm do not match, throws an exception
        if (!isPasswordAndPasswordConfirmMatches(registerRequest)) {
            log.error("Password and password confirm doesn't match");
            throw new PasswordDontMatchException();
        }

        // If an account with the given email already exists, throws an exception
        if (userService.emailExists(registerRequest.email())) {
            log.error("Email already exists");
            throw new EmailAlreadyExistsException();
        }

        try {
            // Create the user
            User user = registerRequest.toUser();
            userService.saveUser(user);

            // Generate a verification code
            VerificationCode verificationCode = verificationCodeService.createVerificationCode(
                registerRequest.email(),
                VerificationCode.CodeType.ACCOUNT_ACTIVATION
            );

            // Send the verification code email
            log.info("Sending activation code {} to user {}", verificationCode.getCode(), registerRequest.email());

            emailService.sendActivationCode(
                registerRequest.email(),
                registerRequest.firstName(),
                verificationCode.getCode()
            );

            log.info("User successfully registered with request {}", registerRequest);
        } catch (Exception e) {
            log.error("Cannot create user with request {}", registerRequest);
            log.error("Error: {}", e.getMessage());
            throw e;
        }
    }


    public void verifyUserAccount(String email, String code) {
        // Verify the code
        verificationCodeService.verifyCode(email, code, VerificationCode.CodeType.ACCOUNT_ACTIVATION);

        // Enable the user
        userService.enableUser(email);

        // Invalidate all activation codes for this email
        verificationCodeService.invalidateAllCodes(email, VerificationCode.CodeType.ACCOUNT_ACTIVATION);
    }

    public void sendResetPasswordCodeToUser(String email) {
        // Find user
        var user = userService.findUserByEmail(email);

        // Generate a verification code
        VerificationCode verificationCode = verificationCodeService.createVerificationCode(
            email,
            VerificationCode.CodeType.PASSWORD_RESET
        );

        try {
            log.info("Sending reset password code to user with email {}", email);
            emailService.sendResetPasswordCode(email, user.getFirstName(), verificationCode.getCode());
        } catch (Exception e) {
            log.warn("Error while sending reset password code to user with email {}", email);
            log.info("If you didn't receive the email, note that the verification code is: {}", verificationCode.getCode());
            throw new MailSendException("Error while sending reset password code to user with email: " + email);
        }

        log.info("Reset password code sent to user with email {}", email);
    }


    public void resetPassword(String email, String code, String newPassword, String confirmPassword) {
        // Verify the code
        verificationCodeService.verifyCode(email, code, VerificationCode.CodeType.PASSWORD_RESET);

        // Update the password
        userService.updatePassword(email, newPassword, confirmPassword);

        // Invalidate all password reset codes for this email
        verificationCodeService.invalidateAllCodes(email, VerificationCode.CodeType.PASSWORD_RESET);
    }


    public boolean isPasswordAndPasswordConfirmMatches(RegisterRequest registerRequest) {
        // checks if the password and password confirm are the same
        return registerRequest.password().equals(registerRequest.confirmPassword());
    }


    /**
     * Refreshes the JWT token.
     *
     * @param request  HTTP request.
     * @param response HTTP response.
     * @return The new JWT token.
     * @throws IOException If an error occurs while writing the response.
     */
    public AuthenticationResponse refreshToken(HttpServletRequest request, HttpServletResponse response) throws IOException {
        // initialize the result
        AuthenticationResponse result = null;

        // extract the token from the request header
        final String authHeader = request.getHeader("Authorization");

        // if the token is null or does not start with "Bearer ", return an error
        if (authHeader == null || !authHeader.startsWith("Bearer ")) {
            response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
            response.getWriter().write("Missing or invalid Authorization header.");
            log.error("Missing or invalid Authorization header.");
        } else { // else, try to refresh the token
            try {
                // extract the refresh token
                log.info("Refreshing token for request {}", request.getHeader("Authorization"));
                final String refreshToken = authHeader.substring(7);

                // extract the user email from the refresh token
                var username = jwtService.extractUsername(refreshToken);
                log.info("User email is {}", username);

                // if the user email is not null, find the user in the database
                if (username != null) {
                    // find the user in the database
                    var userDetails = userService.loadUserByUsername(username);
                    log.info("User is {}", userDetails);

                    // if the user is not null and the refresh token is valid, generate a new access token
                    if (jwtService.isTokenValid(refreshToken, userDetails)) {
                        var accessToken = jwtService.generateAccessToken(userDetails.user()); // generate a new access token
                        log.info("Access token is {}", accessToken);
                        tokenService.revokeAllUserTokens(userDetails.user()); // revoke all user tokens
                        tokenService.saveUserToken(userDetails.user(), accessToken); // save the new access token

                        // set the result
                        result = new AuthenticationResponse(accessToken, refreshToken);
                    }
                }
            } catch (ExpiredJwtException ex) { // if the refresh token is expired, return an error
                log.warn("refresh token expired: {}", ex.getMessage());
                response.sendError(SC_UNAUTHORIZED, "refresh token expired");
            } catch (MalformedJwtException e) { // if the refresh token is invalid, return an error
                log.warn("refresh token expired: {}", e.getMessage());
                response.sendError(SC_UNAUTHORIZED, "invalid refresh token.");
            }
        }
        return result;
    }
}

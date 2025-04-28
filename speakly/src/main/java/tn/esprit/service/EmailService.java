package tn.esprit.service;

import jakarta.annotation.PostConstruct;
import jakarta.mail.MessagingException;
import jakarta.mail.internet.MimeMessage;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.io.ClassPathResource;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.stereotype.Service;
import org.thymeleaf.TemplateEngine;
import tn.esprit.entity.Facture;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.util.Calendar;
import org.thymeleaf.context.Context;
import tn.esprit.entity.Remboursement;
import tn.esprit.utils.PDFUtils;

@Slf4j
@Service
public class EmailService {

    @Value("${spring.mail.username}")
    private String fromEmail;

    @Value("${spring.mail.password}")
    private String emailPassword;

    @Value("${verification-code.expiration.account-activation}")
    private int activationCodeExpirationTimeInMinutes;

    @Value("${verification-code.expiration.reset-password}")
    private int resetPasswordCodeExpirationTimeInMinutes;

    @Value("${jwt.expiration.enable-account}")
    private long enableAccountExpirationTimeInMs;

    @Value("${jwt.expiration.reset-password}")
    private long resetPasswordExpirationTimeInMs;

    private final JavaMailSender mailSender;
    private final TemplateEngine templateEngine;


    @Autowired
    public EmailService(JavaMailSender mailSender,TemplateEngine templateEngine) {
        this.mailSender = mailSender;
        this.templateEngine=templateEngine;
    }

    @PostConstruct
    public void init() {
        System.out.println("Email password length: " + emailPassword.length());
        System.out.println("Password: '" + emailPassword + "'");
    }

    // ========== Basic Email Sending ==========
    public void sendSimpleEmail(String toEmail, String subject, String body) {
        SimpleMailMessage message = new SimpleMailMessage();
        message.setFrom(this.fromEmail);
        message.setTo(toEmail);
        message.setSubject(subject);
        message.setText(body);
        mailSender.send(message);
    }

    public void sendHtmlEmail(String to, String subject, String htmlContent) {
        MimeMessage message = mailSender.createMimeMessage();

        try {
            MimeMessageHelper helper = new MimeMessageHelper(message, true);
            helper.setTo(to);
            helper.setSubject(subject);
            helper.setText(htmlContent, true);
            helper.setFrom(fromEmail);
            mailSender.send(message);
        } catch (MessagingException e) {
            throw new RuntimeException("Erreur lors de l'envoi de l'email", e);
        }
    }

    // ========== Code-Based Emails ==========
    public void sendActivationCode(String email, String firstName, String code) {
        String template = "templates/activate-account-code.html";
        String subject = "Verify Your Account";
        sendEmailWithVerificationCode(email, firstName, subject, code, template, activationCodeExpirationTimeInMinutes);
    }

    public void sendResetPasswordCode(String email, String firstName, String code) {
        String template = "templates/reset-password-code.html";
        String subject = "Reset Your Password";
        sendEmailWithVerificationCode(email, firstName, subject, code, template, resetPasswordCodeExpirationTimeInMinutes);
    }

    private void sendEmailWithVerificationCode(
            String email, String firstName, String subject, String code,
            String template, int expirationMinutes) {

        String currentYear = String.valueOf(Calendar.getInstance().get(Calendar.YEAR));
        String senderName = "Speakly Team";

        try {
            MimeMessage message = mailSender.createMimeMessage();
            MimeMessageHelper helper = new MimeMessageHelper(message, true, "UTF-8");

            helper.setFrom(fromEmail, senderName);
            helper.setTo(email);
            helper.setSubject(subject);

            ClassPathResource resource = new ClassPathResource(template);
            String content = new String(Files.readAllBytes(resource.getFile().toPath()));

            content = content.replace("{{firstName}}", firstName)
                    .replace("{{verificationCode}}", code)
                    .replace("{{currentYear}}", currentYear)
                    .replace("{{expirationTimeInMinutes}}", String.valueOf(expirationMinutes));

            helper.setText(content, true);
            mailSender.send(message);

            log.info("Verification email sent to {}", email);

        } catch (MessagingException | IOException e) {
            log.error("Failed to send verification email to {}", email, e);
        }
    }

    public void sendResetPasswordRequestToUser(String email, String firstName, String resetPasswordLink) {
        String template = "templates/reset-password.html";
        String subject = "Reset Your Password";
        sendEmailWithTemplate(email, firstName, subject, resetPasswordLink, template, resetPasswordExpirationTimeInMs);
    }

    public void sendDocumentVerificationSuccessful(String email, String firstName) {
        String TEMPLATE = "templates/document-verification-success.html";
        String subject = "Document Verified Successfully";

        sendGenericEmail(email, firstName, subject, TEMPLATE, "accepted");
    }

    public void sendDocumentVerificationRejected(String email, String firstName) {
        String TEMPLATE = "templates/document-verification-rejected.html";
        String subject = "Document Verification Rejected";

        sendGenericEmail(email, firstName, subject, TEMPLATE, "rejected");
    }

    public void sendNewMedecinDocumentSubmissionNotification(String email, String firstName) {
        String TEMPLATE = "templates/new-document-submission.html";
        String subject = "New Document Submitted by Medecin";

        sendGenericEmail(email, firstName, subject, TEMPLATE, "submitted");
    }

    public void sendDocumentSubmissionWaitingApproval(String email, String firstName) {
        String TEMPLATE = "templates/document-submission-waiting.html";
        String subject = "Document Submission Received";

        sendGenericEmail(email, firstName, subject, TEMPLATE, "waiting");
    }


    private void sendEmailWithTemplate(
            String email, String firstName, String subject, String url,
            String template, long expirationTimeInMs) {

        String currentYear = String.valueOf(Calendar.getInstance().get(Calendar.YEAR));
        int expirationTimeInMinutes = (int) (expirationTimeInMs / 60000);
        String senderName = "Speakly Team";

        try {
            MimeMessage message = mailSender.createMimeMessage();
            MimeMessageHelper helper = new MimeMessageHelper(message, true, "UTF-8");

            helper.setFrom(fromEmail, senderName);
            helper.setTo(email);
            helper.setSubject(subject);

            ClassPathResource resource = new ClassPathResource(template);
            String content = new String(Files.readAllBytes(resource.getFile().toPath()));

            content = content.replace("{{firstName}}", firstName)
                    .replace("{{activationLink}}", url)
                    .replace("{{currentYear}}", currentYear)
                    .replace("{{expirationTimeInMinutes}}", String.valueOf(expirationTimeInMinutes));

            helper.setText(content, true);
            mailSender.send(message);

            log.info("Email with link sent to {}", email);

        } catch (MessagingException | IOException e) {
            log.error("Failed to send email to {}", email, e);
        }
    }
    public void sendPaymentEmail(String to, String name, Facture facture) throws Exception {
        MimeMessage message = mailSender.createMimeMessage();
        MimeMessageHelper helper = new MimeMessageHelper(message, true, "UTF-8");

        helper.setTo(to);
        helper.setSubject("Confirmation de paiement - Votre facture");

        // Préparation des variables du template
        Context context = new Context();
        context.setVariable("name", name);
        context.setVariable("montant",facture.getMontant() );
        context.setVariable("facture", facture);

        // Génération du contenu HTML
        String htmlContent = templateEngine.process("payment-notification", context);
        helper.setText(htmlContent, true);
        File pdfFile= PDFUtils.generatePdf(facture);
        // Ajout du PDF en pièce jointe
        helper.addAttachment("facture.pdf", pdfFile);

        mailSender.send(message);
    }
    public void sendRemboursementDecisionEmail(String to, String name, Remboursement remboursement) throws Exception {
        MimeMessage message = mailSender.createMimeMessage();
        MimeMessageHelper helper = new MimeMessageHelper(message, true, "UTF-8");

        helper.setTo(to);
        helper.setSubject("Décision concernant votre demande de remboursement");

        // Préparation des variables du template
        Context context = new Context();
        context.setVariable("name", name);
        context.setVariable("montant", remboursement.getFacture().getMontant());
        context.setVariable("status", remboursement.getStatut().compareTo("declined")==0?"REFUSÉ":"ACCEPTÉ"); // "ACCEPTÉ" ou "REFUSÉ"

        if ("declined".equalsIgnoreCase(remboursement.getStatut())) {
            context.setVariable("raisonRefus", remboursement.getRaison());
        }

        // Génération du contenu HTML
        String htmlContent = templateEngine.process("remboursement-decision", context);
        helper.setText(htmlContent, true);

        mailSender.send(message);
    }


    private void sendGenericEmail(String email, String firstName, String subject, String template, String status) {
        String senderName = "Speakly Team";
        String currentYear = String.valueOf(Calendar.getInstance().get(Calendar.YEAR));

        try {
            MimeMessage message = mailSender.createMimeMessage();
            MimeMessageHelper helper = new MimeMessageHelper(message, true, "UTF-8");

            helper.setFrom(fromEmail, senderName);
            helper.setTo(email);
            helper.setSubject(subject);

            // Load email template from file
            ClassPathResource resource = new ClassPathResource(template);
            String content = new String(Files.readAllBytes(resource.getFile().toPath()));

            // Replace placeholders in email template
            content = content.replace("{{firstName}}", firstName);
            content = content.replace("{{status}}", status);
            content = content.replace("{{currentYear}}", currentYear);

            helper.setText(content, true);

            mailSender.send(message);
            log.info("{} email sent to {}", status, email);

        } catch (MessagingException | IOException e) {
            log.error("Failed to send {} email to {}", status, email, e);
        }
    }


}
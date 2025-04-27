package tn.esprit.controller;

import com.stripe.exception.StripeException;
import com.stripe.model.PaymentIntent;
import com.stripe.param.PaymentIntentCreateParams;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import tn.esprit.entity.Facture;
import tn.esprit.entity.User;
import tn.esprit.service.EmailService;
import tn.esprit.service.FactureService;

import java.util.HashMap;
import java.util.Map;
import java.util.Objects;

@RestController
@RequestMapping("/payment")
@RequiredArgsConstructor
public class PaymentController {

    @Autowired
    EmailService mailingService;
    @Autowired
    FactureService factureService;

    @PostMapping("/create-payment-intent")
    public ResponseEntity<Map<String, String>> createPayment(@RequestBody Map<String, Object> request)
            throws StripeException {
        int amout = (int) request.get("amount") * 100;
        PaymentIntentCreateParams params = PaymentIntentCreateParams.builder().setAmount((long) amout)
                .setCurrency("usd").setAutomaticPaymentMethods(
                        PaymentIntentCreateParams.AutomaticPaymentMethods.builder().setEnabled(true).build())
                .build();
        PaymentIntent intent = PaymentIntent.create(params);
        Map<String, String> response = new HashMap<>();
        response.put("clientSecret", intent.getClientSecret());
        return ResponseEntity.ok(response);
    }

    @PutMapping("/confirmPayment/{factureId}")
    public ResponseEntity<Facture> confirmPaymeent(@PathVariable Long factureId) throws Exception {
        Facture f = factureService.updateStatus(factureId, "paid");
        if(f.getPatient()!=null){
            String email =f.getPatient().getEmail();
            String name=f.getPatient().getFirstName()+f.getPatient().getLastName();
            mailingService.sendPaymentEmail(email, name, f);
        }
        return ResponseEntity.ok(f);

    }

}

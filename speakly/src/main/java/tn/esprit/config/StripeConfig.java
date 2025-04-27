package tn.esprit.config;

import com.stripe.Stripe;
import jakarta.annotation.PostConstruct;
import org.springframework.context.annotation.Configuration;

@Configuration
public class StripeConfig {
    @PostConstruct
    public void init() {
        Stripe.apiKey="sk_test_51RFup22YjJuq8v3GobF0m8yFGt3N6qodB2JC1nq8mGv4McZxwGLzsLDug5YWFB6YFg1NhWDs4ZxPMSne5x2Aqjyl00XfZIa54b";
    }
}

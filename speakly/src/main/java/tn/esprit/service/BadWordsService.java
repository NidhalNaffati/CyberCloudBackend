package tn.esprit.service;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Service
public class BadWordsService {

    @Value("${badwords.api.url}")
    private String badWordsApiUrl;

    @Value("${badwords.api.key}")
    private String apiKey;

    private final RestTemplate restTemplate;

    public BadWordsService(RestTemplate restTemplate) {
        this.restTemplate = restTemplate;
    }

    public String checkForBadWords(String content) {
        // Prepare the request payload (unchanged)
        Map<String, Object> requestPayload = new HashMap<>();
        requestPayload.put("model", "openai/gpt-3.5-turbo");
        requestPayload.put("messages", new Object[]{
                Map.of("role", "system", "content", "Respond only with \"YES\" if the text contains inappropriate words, otherwise \"NO\"."),
                Map.of("role", "user", "content", content)
        });
        requestPayload.put("max_tokens", 5);

        // Set headers (unchanged)
        HttpHeaders headers = new HttpHeaders();
        headers.set("Authorization", "Bearer " + apiKey);
        headers.set("Content-Type", "application/json");

        // Make the HTTP POST request
        HttpEntity<Map<String, Object>> request = new HttpEntity<>(requestPayload, headers);
        ResponseEntity<Map> response = restTemplate.postForEntity(badWordsApiUrl, request, Map.class);

        // Extract and return the response (FIXED)
        if (response.getBody() != null && response.getBody().containsKey("choices")) {
            List<Map<String, Object>> choices = (List<Map<String, Object>>) response.getBody().get("choices");
            if (!choices.isEmpty()) {
                Map<String, Object> firstChoice = choices.get(0);
                Map<String, Object> message = (Map<String, Object>) firstChoice.get("message");
                return (String) message.get("content");
            }
        }
        return "NO";
    }
}
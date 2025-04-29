package tn.esprit.service;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.*;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Service
public class GenerateContentService {

    @Value("${GenerateContent.api.url}")
    private String generateContentApiUrl;

    @Value("${GenerateContent.api.key}")
    private String apiKey;

    private final RestTemplate restTemplate;
    private final ObjectMapper objectMapper;

    public GenerateContentService(RestTemplate restTemplate) {
        this.restTemplate = restTemplate;
        this.objectMapper = new ObjectMapper();
    }

    /**
     * Generates content based on a given subject using the Hugging Face API
     * @param subject The subject to generate content for
     * @return The generated content
     */
    public String generateContent(String subject) {
        try {
            System.out.println("Generating content for subject: " + subject);
            
            // Create a more specific prompt for better results
            String prompt = "Generate a detailed and professional feedback content based on this subject: '" + subject + "'. "
                    + "The content should be well-structured, informative, and about 3-4 paragraphs long.";
            
            // Prepare the request payload for Hugging Face API
            Map<String, Object> requestPayload = new HashMap<>();
            requestPayload.put("inputs", prompt);
            requestPayload.put("parameters", Map.of(
                "max_length", 500,
                "temperature", 0.7,
                "top_p", 0.9
            ));
            
            // Set headers
            HttpHeaders headers = new HttpHeaders();
            headers.set("Authorization", "Bearer " + apiKey);
            headers.setContentType(MediaType.APPLICATION_JSON);
            headers.setAccept(List.of(MediaType.APPLICATION_JSON));

            // Make the HTTP POST request
            HttpEntity<Map<String, Object>> request = new HttpEntity<>(requestPayload, headers);
            
            // Log the request for debugging
            System.out.println("Request URL: " + generateContentApiUrl);
            System.out.println("Request headers: " + headers);
            System.out.println("Request body: " + objectMapper.writeValueAsString(requestPayload));
            
            // Make the request and get raw response
            ResponseEntity<String> response = restTemplate.exchange(
                generateContentApiUrl,
                HttpMethod.POST,
                request,
                String.class
            );
            
            // Log the raw response
            System.out.println("Response status: " + response.getStatusCode());
            System.out.println("Response body: " + response.getBody());
            
            if (response.getStatusCode().is2xxSuccessful() && response.getBody() != null) {
                // Try to parse as a list first (common Hugging Face response format)
                try {
                    List<Map<String, Object>> responseList = objectMapper.readValue(response.getBody(), List.class);
                    if (!responseList.isEmpty()) {
                        Map<String, Object> firstResult = responseList.get(0);
                        if (firstResult.containsKey("generated_text")) {
                            return (String) firstResult.get("generated_text");
                        }
                    }
                } catch (Exception e) {
                    System.out.println("Not a list response: " + e.getMessage());
                }
                
                // Try to parse as a single object
                try {
                    Map<String, Object> responseMap = objectMapper.readValue(response.getBody(), Map.class);
                    if (responseMap.containsKey("generated_text")) {
                        return (String) responseMap.get("generated_text");
                    }
                } catch (Exception e) {
                    System.out.println("Not a map response: " + e.getMessage());
                }
                
                // If we can't parse it as JSON, return the raw response
                return response.getBody();
            }
            
            return "Could not generate content for the given subject. Please try again.";
        } catch (Exception e) {
            System.err.println("Error generating content: " + e.getMessage());
            e.printStackTrace();
            return "An error occurred while generating content. Please try again later.";
        }
    }
}

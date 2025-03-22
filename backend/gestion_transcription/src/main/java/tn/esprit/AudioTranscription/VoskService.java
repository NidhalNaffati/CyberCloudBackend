package tn.esprit.AudioTranscription;

import jakarta.annotation.PostConstruct;
import jakarta.annotation.PreDestroy;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.vosk.Model;
import org.vosk.Recognizer;

import java.io.IOException;

@Slf4j
@Service
public class VoskService {

    @Value("${vosk.model.path}")
    private String modelPath;

    private Model model;

    @PostConstruct
    public void init() {
        try {
            log.info("Loading Vosk model from: {}", modelPath);
            model = new Model(modelPath);
            log.info("Vosk model loaded successfully");
        } catch (IOException e) {
            log.error("Failed to load Vosk model", e);
            throw new RuntimeException("Failed to initialize Vosk model", e);
        }
    }

    @PreDestroy
    public void cleanup() {
        if (model != null) {
            model.close();
            log.info("Vosk model closed");
        }
    }

    public String recognizeSpeech(byte[] audioData) {
        log.info("Processing audio chunk of {} bytes", audioData.length);

        String result = "";

        // Create a new recognizer for each request
        try (Recognizer recognizer = new Recognizer(model, 16000)) {
            // Set properties for better results
            recognizer.setMaxAlternatives(0);
            recognizer.setWords(true);

            // Process the audio data
            boolean finalResult = recognizer.acceptWaveForm(audioData, audioData.length);

            if (finalResult) {
                result = recognizer.getResult();
                log.info("Final Result: {}", result);
                return result;
            } else {
                String partialResult = recognizer.getPartialResult();
                log.info("Partial Result: {}", partialResult);
                return partialResult;
            }
        } catch (Exception e) {
            log.error("Error processing audio with Vosk", e);
            throw new RuntimeException("Failed to recognize speech: " + e.getMessage(), e);
        }
    }

    // Helper method to check audio data format
    private void logAudioDetails(byte[] audioData) {
        if (audioData.length > 20) {
            // Log the first few bytes to check format
            StringBuilder hexDump = new StringBuilder("Audio data (first 20 bytes): ");
            for (int i = 0; i < 20; i++) {
                hexDump.append(String.format("%02X ", audioData[i] & 0xFF));
            }
            log.info(hexDump.toString());

            // Check if there's any non-zero data
            boolean hasNonZero = false;
            for (byte b : audioData) {
                if (b != 0) {
                    hasNonZero = true;
                    break;
                }
            }
            log.info("Audio data contains non-zero values: {}", hasNonZero);
        }
    }
}
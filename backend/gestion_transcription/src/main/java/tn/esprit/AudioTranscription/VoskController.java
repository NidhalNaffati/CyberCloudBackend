package tn.esprit.AudioTranscription;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.messaging.handler.annotation.MessageMapping;
import org.springframework.messaging.handler.annotation.SendTo;
import org.springframework.stereotype.Controller;

import java.util.Base64;

@Controller
@RequiredArgsConstructor
@Slf4j
public class VoskController {

    private final VoskService voskService;

    @MessageMapping("/transcribe")
    @SendTo("/topic/transcription")
    public TranscriptionResult transcribe(AudioMessage audioMessage) {
        try {
            // Log the size of the audio data
            log.info("Received audio data of size: {} bytes", audioMessage.getAudioData().length());

            // Decode the Base64 audio data
            byte[] audioData = Base64.getDecoder().decode(audioMessage.getAudioData());
            log.info("Decoded audio data size: {} bytes", audioData.length);

            // Check if we have valid audio data
            if (audioData.length == 0) {
                log.warn("Empty audio data received");
                return new TranscriptionResult("Empty audio data", false);
            }

            // Process audio with Vosk
            String result = voskService.recognizeSpeech(audioData);

            // Return the result
            return new TranscriptionResult(result, true);
        } catch (Exception e) {
            log.error("Error processing audio data", e);
            return new TranscriptionResult("Error: " + e.getMessage(), false);
        }
    }
}
package tn.esprit.AudioTranscription;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class AudioMessage {
    private String audioData; // Base64 encoded audio data
}


package tn.esprit.AudioTranscription;


import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class TranscriptionResult {
    private String text;
    private boolean success;
}
package tn.esprit.DTO;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.Date;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class MedecinDocumentDto {
    private Long id;
    private String documentName;
    private String documentType;
    private Long documentSize;
    private String documentData; // Base64 encoded document data
    private Date uploadDate;
    private Boolean userDocumentsVerified;
}

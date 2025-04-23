package tn.esprit.dto;

import lombok.AllArgsConstructor;
import lombok.Data;

import java.util.Date;

@Data
@AllArgsConstructor
public class DocumentDTO {
    private Long id;
    private String documentName;
    private String documentType;
    private Long documentSize;
    private Date uploadDate;
}
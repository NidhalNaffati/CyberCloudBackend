package tn.esprit.gestion_blog.dto;

import lombok.*;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class ImageDTO {
    private Long imageId;
    private String name;
    private String type;
    private Integer orderIndex;
    private String imageUrl; // URL construite vers l'image
}
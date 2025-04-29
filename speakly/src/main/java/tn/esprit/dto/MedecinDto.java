package tn.esprit.DTO;

import lombok.*;
import tn.esprit.entity.Role;

import java.util.Date;

@Builder
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class MedecinDto {
    private Long id;
    private String firstName;
    private String lastName;
    private String email;
    private Role role;
    private boolean enabled;
    private boolean accountNonLocked;
    private Date createdAt;
    private Boolean documentsVerified;
}


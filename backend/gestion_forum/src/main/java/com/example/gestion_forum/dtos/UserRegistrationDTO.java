package com.example.gestion_forum.dtos;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class UserRegistrationDTO {
    private String username;
    private String email;
    private String password;
}

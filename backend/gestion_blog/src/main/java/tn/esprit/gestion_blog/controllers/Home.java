package tn.esprit.gestion_blog.controllers;


import lombok.AllArgsConstructor;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@AllArgsConstructor
public class Home {

    @GetMapping
    public String home() {
        return "Hello World";
    }
}

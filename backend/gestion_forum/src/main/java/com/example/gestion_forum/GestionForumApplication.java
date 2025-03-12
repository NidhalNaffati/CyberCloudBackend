package com.example.gestion_forum;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.data.jpa.repository.config.EnableJpaRepositories;



@SpringBootApplication
@EnableJpaRepositories(basePackages = "com.example.gestion_forum.repository") // ✅ Ensure correct package
public class GestionForumApplication {
	public static void main(String[] args) {
		SpringApplication.run(GestionForumApplication.class, args);
	}
}

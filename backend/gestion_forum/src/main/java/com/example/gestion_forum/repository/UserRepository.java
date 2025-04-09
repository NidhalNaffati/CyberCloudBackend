package com.example.gestion_forum.repository;  // ✅ Ensure the correct package

import com.example.gestion_forum.models.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import java.util.Optional;

@Repository  // ✅ Mark as a Spring Repository
public interface UserRepository extends JpaRepository<User, Long> {
    Optional<User> findByUsername(String username);  // ✅ Ensure this method exists
}

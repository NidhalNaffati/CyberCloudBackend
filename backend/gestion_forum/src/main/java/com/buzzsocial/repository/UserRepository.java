package com.buzzsocial.repository;

import com.buzzsocial.model.User;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface UserRepository extends JpaRepository<User, String> {
    Optional<User> findByUsername(String username);
    Optional<User> findByEmail(String email);
    boolean existsByUsername(String username);
    boolean existsByEmail(String email);
    
    @Query("SELECT u FROM User u WHERE u.username LIKE %:query% OR u.fullName LIKE %:query% OR u.email LIKE %:query%")
    List<User> searchUsers(String query);
    
    @Query(value = "SELECT * FROM users ORDER BY RAND() LIMIT :limit", nativeQuery = true)
    List<User> findRandomUsers(int limit);
    
    long countByIsBanned(boolean isBanned);
}

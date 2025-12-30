package com.bac.backend.repository;

import com.bac.backend.model.User;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.Optional;

public interface UserRepository extends JpaRepository<User, Long> {
    // Mencari user berdasarkan NIM
    Optional<User> findByNim(String nim);
}
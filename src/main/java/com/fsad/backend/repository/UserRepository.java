package com.fsad.backend.repository;

import com.fsad.backend.entity.User;
import com.fsad.backend.entity.UserRole;
import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;

public interface UserRepository extends JpaRepository<User, Long> {
    Optional<User> findByEmail(String email);

    boolean existsByNameIgnoreCaseAndRole(String name, UserRole role);
}

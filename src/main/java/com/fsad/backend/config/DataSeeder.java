package com.fsad.backend.config;

import com.fsad.backend.entity.User;
import com.fsad.backend.entity.UserRole;
import com.fsad.backend.repository.UserRepository;
import org.springframework.boot.CommandLineRunner;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.crypto.password.PasswordEncoder;

@Configuration
public class DataSeeder {

    @Bean
    CommandLineRunner seedUsers(UserRepository userRepository, PasswordEncoder passwordEncoder) {
        return args -> {
            if (userRepository.count() > 0) {
                return;
            }

            userRepository.save(buildUser("Avery Admin", "admin@auroragrade.local", "admin123", UserRole.ADMIN, passwordEncoder));
            userRepository.save(buildUser("Taylor Teacher", "teacher@auroragrade.local", "teacher123", UserRole.TEACHER, passwordEncoder));
            userRepository.save(buildUser("Sam Student", "student@auroragrade.local", "student123", UserRole.STUDENT, passwordEncoder));
        };
    }

    private User buildUser(String name, String email, String password, UserRole role, PasswordEncoder encoder) {
        User user = new User();
        user.setName(name);
        user.setEmail(email);
        user.setPassword(encoder.encode(password));
        user.setRole(role);
        return user;
    }
}

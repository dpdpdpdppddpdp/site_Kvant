package com.kvant;

import com.kvant.entity.User;
import com.kvant.repository.UserRepository;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;
import org.springframework.scheduling.annotation.EnableAsync;
import org.springframework.security.crypto.password.PasswordEncoder;

@SpringBootApplication
@EnableAsync
public class KvantApplication {
    public static void main(String[] args) {
        SpringApplication.run(KvantApplication.class, args);
    }

    @Bean
    CommandLineRunner initAdmin(UserRepository userRepository, PasswordEncoder passwordEncoder) {
        return args -> {
            if (!userRepository.existsByUsername("admin")) {
                User admin = new User();
                admin.setUsername("admin");
                admin.setPassword(passwordEncoder.encode("admin123"));
                admin.setEmail("admin@kvant.ru");
                admin.setFirstName("Admin");
                admin.setLastName("Admin");
                admin.setPhone("+79999999999");
                admin.setRole(User.Role.ADMIN);
                userRepository.save(admin);
                System.out.println("=== Admin created: username=admin, password=admin123 ===");
            } else {
                User admin = userRepository.findByUsername("admin").orElseThrow();
                admin.setRole(User.Role.ADMIN);
                admin.setPassword(passwordEncoder.encode("admin123"));
                admin.setEnabled(true);
                admin.setAccountNonLocked(true);
                admin.setAccountNonExpired(true);
                admin.setCredentialsNonExpired(true);
                userRepository.save(admin);
                System.out.println("=== Admin reset: username=admin, password=admin123, role=ADMIN ===");
            }
        };
    }
}
package com.kvant.controller;

import com.kvant.entity.User;
import com.kvant.repository.UserRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/users")
@CrossOrigin(origins = "*")
public class UserManagementController {

    private static final Logger logger = LoggerFactory.getLogger(UserManagementController.class);

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;

    public UserManagementController(UserRepository userRepository, PasswordEncoder passwordEncoder) {
        this.userRepository = userRepository;
        this.passwordEncoder = passwordEncoder;
    }

    @GetMapping
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<List<User>> getAllUsers() {
        return ResponseEntity.ok(userRepository.findAll());
    }

    @GetMapping("/{id}")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<?> getUserById(@PathVariable Long id) {
        return userRepository.findById(id)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }

    @PutMapping("/{id}")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<?> updateUser(@PathVariable Long id, @RequestBody Map<String, String> request) {
        try {
            User user = userRepository.findById(id)
                    .orElseThrow(() -> new RuntimeException("Пользователь не найден"));

            if (request.containsKey("firstName")) {
                user.setFirstName(request.get("firstName"));
            }
            if (request.containsKey("lastName")) {
                user.setLastName(request.get("lastName"));
            }
            if (request.containsKey("email")) {
                user.setEmail(request.get("email"));
            }
            if (request.containsKey("phone")) {
                user.setPhone(request.get("phone"));
            }
            if (request.containsKey("role")) {
                user.setRole(User.Role.valueOf(request.get("role")));
            }

            userRepository.save(user);
            logger.info("User updated by admin: {}", user.getUsername());

            return ResponseEntity.ok(user);
        } catch (Exception e) {
            logger.error("Error updating user: {}", e.getMessage(), e);
            return ResponseEntity.badRequest().body(Map.of("error", "Ошибка обновления пользователя"));
        }
    }

    @DeleteMapping("/{id}")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<?> deleteUser(@PathVariable Long id) {
        try {
            if (!userRepository.existsById(id)) {
                return ResponseEntity.notFound().build();
            }

            userRepository.deleteById(id);
            logger.info("User deleted by admin: {}", id);

            return ResponseEntity.ok(Map.of("message", "Пользователь успешно удален"));
        } catch (Exception e) {
            logger.error("Error deleting user: {}", e.getMessage(), e);
            return ResponseEntity.badRequest().body(Map.of("error", "Ошибка удаления пользователя"));
        }
    }

    @PostMapping("/{id}/block")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<?> blockUser(@PathVariable Long id) {
        try {
            User user = userRepository.findById(id)
                    .orElseThrow(() -> new RuntimeException("Пользователь не найден"));

            user.setEnabled(false);
            user.setAccountNonLocked(false);
            userRepository.save(user);

            logger.info("User blocked by admin: {}", user.getUsername());

            return ResponseEntity.ok(Map.of("message", "Пользователь заблокирован"));
        } catch (Exception e) {
            logger.error("Error blocking user: {}", e.getMessage(), e);
            return ResponseEntity.badRequest().body(Map.of("error", "Ошибка блокировки пользователя"));
        }
    }

    @PostMapping("/{id}/unblock")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<?> unblockUser(@PathVariable Long id) {
        try {
            User user = userRepository.findById(id)
                    .orElseThrow(() -> new RuntimeException("Пользователь не найден"));

            user.setEnabled(true);
            user.setAccountNonLocked(true);
            userRepository.save(user);

            logger.info("User unblocked by admin: {}", user.getUsername());

            return ResponseEntity.ok(Map.of("message", "Пользователь разблокирован"));
        } catch (Exception e) {
            logger.error("Error unblocking user: {}", e.getMessage(), e);
            return ResponseEntity.badRequest().body(Map.of("error", "Ошибка разблокировки пользователя"));
        }
    }

    @PostMapping("/{id}/reset-password")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<?> resetPassword(@PathVariable Long id, @RequestBody Map<String, String> request) {
        try {
            User user = userRepository.findById(id)
                    .orElseThrow(() -> new RuntimeException("Пользователь не найден"));

            String newPassword = request.get("newPassword");
            if (newPassword == null || newPassword.length() < 8) {
                return ResponseEntity.badRequest().body(Map.of("error", "Пароль должен содержать минимум 8 символов"));
            }

            user.setPassword(passwordEncoder.encode(newPassword));
            userRepository.save(user);

            logger.info("Password reset by admin for user: {}", user.getUsername());

            return ResponseEntity.ok(Map.of("message", "Пароль успешно сброшен"));
        } catch (Exception e) {
            logger.error("Error resetting password: {}", e.getMessage(), e);
            return ResponseEntity.badRequest().body(Map.of("error", "Ошибка сброса пароля"));
        }
    }
}

package com.kvant.controller;

import com.kvant.entity.User;
import com.kvant.repository.UserRepository;
import com.kvant.service.EmailService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDateTime;
import java.util.Map;
import java.util.Random;
import java.util.concurrent.ConcurrentHashMap;

@RestController
@RequestMapping("/api/auth")
@CrossOrigin(origins = "*")
public class AuthController {

    private static final Logger logger = LoggerFactory.getLogger(AuthController.class);

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    private final EmailService emailService;

    private static final int CODE_TTL_MINUTES = 10;

    private static class PendingRegistration {
        String username, password, email, firstName, lastName, phone, code;
        LocalDateTime expiresAt;

        PendingRegistration(String username, String password, String email,
                            String firstName, String lastName, String phone, String code) {
            this.username = username; this.password = password; this.email = email;
            this.firstName = firstName; this.lastName = lastName; this.phone = phone;
            this.code = code;
            this.expiresAt = LocalDateTime.now().plusMinutes(CODE_TTL_MINUTES);
        }

        boolean isExpired() { return LocalDateTime.now().isAfter(expiresAt); }
    }

    private final ConcurrentHashMap<String, PendingRegistration> pendingMap = new ConcurrentHashMap<>();

    public AuthController(UserRepository userRepository, PasswordEncoder passwordEncoder, EmailService emailService) {
        this.userRepository = userRepository;
        this.passwordEncoder = passwordEncoder;
        this.emailService = emailService;
    }

    @PostMapping("/send-code")
    public ResponseEntity<?> sendVerificationCode(@RequestBody Map<String, String> request) {
        try {
            String username = request.get("username");
            String email    = request.get("email");
            String password = request.get("password");
            String firstName = request.getOrDefault("firstName", "");
            String lastName  = request.getOrDefault("lastName", "");
            String phone     = request.getOrDefault("phone", "");

            if (username == null || username.length() < 3)
                return ResponseEntity.badRequest().body(Map.of("error", "Имя пользователя слишком короткое"));
            if (password == null || password.length() < 8)
                return ResponseEntity.badRequest().body(Map.of("error", "Пароль должен быть не менее 8 символов"));
            if (email == null || !email.contains("@"))
                return ResponseEntity.badRequest().body(Map.of("error", "Некорректный email"));

            if (userRepository.existsByUsername(username))
                return ResponseEntity.badRequest().body(Map.of("error", "Имя пользователя уже занято"));
            if (userRepository.existsByEmail(email))
                return ResponseEntity.badRequest().body(Map.of("error", "Email уже зарегистрирован"));

            String code = String.format("%06d", new Random().nextInt(1_000_000));
            pendingMap.put(email, new PendingRegistration(username, password, email, firstName, lastName, phone, code));

            emailService.sendVerificationCode(email, code);
            logger.info("Verification code sent to {}", email);

            return ResponseEntity.ok(Map.of("message", "Код подтверждения отправлен на " + email));
        } catch (Exception e) {
            logger.error("Error sending verification code: {}", e.getMessage(), e);
            return ResponseEntity.status(500).body(Map.of("error", "Не удалось отправить код: " + e.getMessage()));
        }
    }

    @PostMapping("/register")
    public ResponseEntity<?> registerUser(@RequestBody Map<String, String> request) {
        try {
            String email = request.get("email");
            String code  = request.get("code");

            if (email == null || code == null)
                return ResponseEntity.badRequest().body(Map.of("error", "Необходимо указать email и код"));

            PendingRegistration pending = pendingMap.get(email);
            if (pending == null)
                return ResponseEntity.badRequest().body(Map.of("error", "Сначала запросите код подтверждения"));
            if (pending.isExpired()) {
                pendingMap.remove(email);
                return ResponseEntity.badRequest().body(Map.of("error", "Код истёк. Запросите новый код"));
            }
            if (!pending.code.equals(code.trim()))
                return ResponseEntity.badRequest().body(Map.of("error", "Неверный код подтверждения"));

            if (userRepository.existsByUsername(pending.username))
                return ResponseEntity.badRequest().body(Map.of("error", "Имя пользователя уже занято"));
            if (userRepository.existsByEmail(pending.email))
                return ResponseEntity.badRequest().body(Map.of("error", "Email уже зарегистрирован"));

            User user = new User();
            user.setUsername(pending.username);
            user.setPassword(passwordEncoder.encode(pending.password));
            user.setEmail(pending.email);
            user.setFirstName(pending.firstName.isEmpty() ? null : pending.firstName);
            user.setLastName(pending.lastName.isEmpty() ? null : pending.lastName);
            user.setPhone(pending.phone.isEmpty() ? null : pending.phone);
            user.setRole(User.Role.USER);

            userRepository.save(user);
            pendingMap.remove(email);
            logger.info("New user registered: {}", pending.username);

            return ResponseEntity.ok(Map.of("message", "Регистрация успешна! Теперь вы можете войти."));
        } catch (Exception e) {
            logger.error("Error registering user: {}", e.getMessage(), e);
            return ResponseEntity.badRequest().body(Map.of("error", "Ошибка при регистрации: " + e.getMessage()));
        }
    }

    @PostMapping("/register-direct")
    public ResponseEntity<?> registerDirect(@RequestBody Map<String, String> request) {
        try {
            String username  = request.get("username");
            String email     = request.get("email");
            String password  = request.get("password");
            String firstName = request.getOrDefault("firstName", "");
            String lastName  = request.getOrDefault("lastName", "");
            String phone     = request.getOrDefault("phone", "");

            if (username == null || username.length() < 3)
                return ResponseEntity.badRequest().body(Map.of("error", "Имя пользователя слишком короткое"));
            if (password == null || password.length() < 8)
                return ResponseEntity.badRequest().body(Map.of("error", "Пароль должен быть не менее 8 символов"));
            if (email == null || !email.contains("@"))
                return ResponseEntity.badRequest().body(Map.of("error", "Некорректный email"));
            if (userRepository.existsByUsername(username))
                return ResponseEntity.badRequest().body(Map.of("error", "Имя пользователя уже занято"));
            if (userRepository.existsByEmail(email))
                return ResponseEntity.badRequest().body(Map.of("error", "Email уже зарегистрирован"));

            User user = new User();
            user.setUsername(username);
            user.setPassword(passwordEncoder.encode(password));
            user.setEmail(email);
            user.setFirstName(firstName.isEmpty() ? null : firstName);
            user.setLastName(lastName.isEmpty() ? null : lastName);
            user.setPhone(phone.isEmpty() ? null : phone);
            user.setRole(User.Role.USER);
            userRepository.save(user);
            logger.info("New user registered (direct): {}", username);
            return ResponseEntity.ok(Map.of("message", "Регистрация успешна!"));
        } catch (Exception e) {
            logger.error("Error in register-direct: {}", e.getMessage(), e);
            return ResponseEntity.badRequest().body(Map.of("error", "Ошибка при регистрации: " + e.getMessage()));
        }
    }

    @PostMapping("/create-admin")
    public ResponseEntity<?> createAdmin(@RequestBody Map<String, String> request) {
        try {
            String username = request.get("username");
            String password = request.get("password");
            String email = request.get("email");
            String firstName = request.get("firstName");
            String lastName = request.get("lastName");
            String phone = request.get("phone");

            if (userRepository.existsByUsername(username)) {
                return ResponseEntity.badRequest().body(Map.of("error", "Пользователь с таким именем уже существует"));
            }

            User user = new User();
            user.setUsername(username);
            user.setPassword(passwordEncoder.encode(password));
            user.setEmail(email);
            user.setFirstName(firstName);
            user.setLastName(lastName);
            user.setPhone(phone);
            user.setRole(User.Role.ADMIN);

            userRepository.save(user);
            logger.info("New admin created: {}", username);

            return ResponseEntity.ok(Map.of("message", "Администратор успешно создан"));
        } catch (Exception e) {
            logger.error("Error creating admin: {}", e.getMessage(), e);
            return ResponseEntity.badRequest().body(Map.of("error", "Ошибка при создании администратора: " + e.getMessage()));
        }
    }

    @GetMapping("/check-username")
    public ResponseEntity<?> checkUsername(@RequestParam String username) {
        try {
            boolean available = !userRepository.existsByUsername(username);
            return ResponseEntity.ok(Map.of("available", available));
        } catch (Exception e) {
            logger.error("Error checking username: {}", e.getMessage(), e);
            return ResponseEntity.badRequest().body(Map.of("error", "Ошибка проверки"));
        }
    }

    @GetMapping("/check-email")
    public ResponseEntity<?> checkEmail(@RequestParam String email) {
        try {
            boolean available = !userRepository.existsByEmail(email);
            return ResponseEntity.ok(Map.of("available", available));
        } catch (Exception e) {
            logger.error("Error checking email: {}", e.getMessage(), e);
            return ResponseEntity.badRequest().body(Map.of("error", "Ошибка проверки"));
        }
    }

    @GetMapping("/me")
    public ResponseEntity<?> getCurrentUser(Authentication authentication) {
        try {
            if (authentication != null && authentication.getPrincipal() instanceof User) {
                User user = (User) authentication.getPrincipal();
                return ResponseEntity.ok(user);
            }
            return ResponseEntity.status(401).body(Map.of("error", "Не авторизован"));
        } catch (Exception e) {
            logger.error("Error getting current user: {}", e.getMessage(), e);
            return ResponseEntity.badRequest().body(Map.of("error", "Ошибка получения данных"));
        }
    }

    @PutMapping("/update-profile")
    public ResponseEntity<?> updateProfile(@RequestBody Map<String, String> request, Authentication authentication) {
        try {
            if (authentication == null || !(authentication.getPrincipal() instanceof User)) {
                return ResponseEntity.status(401).body(Map.of("error", "Не авторизован"));
            }

            User user = (User) authentication.getPrincipal();
            user.setFirstName(request.get("firstName"));
            user.setLastName(request.get("lastName"));
            user.setPhone(request.get("phone"));

            userRepository.save(user);
            logger.info("Profile updated for user: {}", user.getUsername());

            return ResponseEntity.ok(Map.of("message", "Профиль успешно обновлен"));
        } catch (Exception e) {
            logger.error("Error updating profile: {}", e.getMessage(), e);
            return ResponseEntity.badRequest().body(Map.of("error", "Ошибка обновления профиля"));
        }
    }

    @PostMapping("/change-password")
    public ResponseEntity<?> changePassword(@RequestBody Map<String, String> request, Authentication authentication) {
        try {
            if (authentication == null || !(authentication.getPrincipal() instanceof User)) {
                return ResponseEntity.status(401).body(Map.of("error", "Не авторизован"));
            }

            User user = (User) authentication.getPrincipal();
            String currentPassword = request.get("currentPassword");
            String newPassword = request.get("newPassword");

            if (!passwordEncoder.matches(currentPassword, user.getPassword())) {
                return ResponseEntity.badRequest().body(Map.of("error", "Неверный текущий пароль"));
            }

            user.setPassword(passwordEncoder.encode(newPassword));
            userRepository.save(user);
            logger.info("Password changed for user: {}", user.getUsername());

            return ResponseEntity.ok(Map.of("message", "Пароль успешно изменен"));
        } catch (Exception e) {
            logger.error("Error changing password: {}", e.getMessage(), e);
            return ResponseEntity.badRequest().body(Map.of("error", "Ошибка смены пароля"));
        }
    }
}

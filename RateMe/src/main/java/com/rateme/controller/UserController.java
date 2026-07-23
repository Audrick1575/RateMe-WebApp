package com.rateme.controller;

import com.rateme.dto.LoginRequest;
import com.rateme.dto.RegisterRequest;
import com.rateme.dto.UserDTO;
import com.rateme.entity.User;
import com.rateme.service.UserService;
import jakarta.servlet.http.HttpSession;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

@RestController
@RequestMapping("/api")
@CrossOrigin(originPatterns = "*", allowCredentials = "true")
public class UserController {

    private static final Logger logger = LoggerFactory.getLogger(UserController.class);
    private final UserService userService;

    public UserController(UserService userService) {
        this.userService = userService;
    }

    // ===================== INSCRIPTION =====================
    @PostMapping("/register")
    public ResponseEntity<?> register(@RequestBody RegisterRequest request) {
        try {
            User user = userService.register(request);
            return ResponseEntity.ok(toDTO(user));
        } catch (Exception e) {
            logger.error("register() – Fehler: {}", e.getMessage());
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(e.getMessage());
        }
    }

    // ===================== CONNEXION =====================
    @PostMapping("/login")
    public ResponseEntity<?> login(@RequestBody LoginRequest request, HttpSession session) {
        try {
            User user = userService.login(request, session);
            return ResponseEntity.ok(toDTO(user));
        } catch (Exception e) {
            logger.error("login() – Fehler: {}", e.getMessage());
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(e.getMessage());
        }
    }

    // ===================== DÉCONNEXION =====================
    @PostMapping("/logout")
    public ResponseEntity<?> logout(HttpSession session) {
        userService.logout(session);
        return ResponseEntity.ok("Logged out successfully");
    }

    // ===================== RÉCUPÉRER L'UTILISATEUR CONNECTÉ =====================
    @GetMapping("/users/me")
    public ResponseEntity<?> getCurrentUser(HttpSession session) {
        User user = userService.getCurrentUser(session);
        if (user == null) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("Not logged in");
        }
        return ResponseEntity.ok(toDTO(user));
    }

    // ===================== SUPPRESSION DU COMPTE (BONUS 4) =====================
    @DeleteMapping("/users/me")
    public ResponseEntity<?> deleteOwnAccount(HttpSession session) {
        User user = userService.getCurrentUser(session);
        if (user == null) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("Not logged in");
        }
        try {
            userService.deleteUser(user);
            session.invalidate(); // déconnexion automatique
            logger.info("deleteOwnAccount() – Benutzer {} gelöscht", user.getUsername());
            return ResponseEntity.ok("Konto erfolgreich gelöscht");
        } catch (Exception e) {
            logger.error("deleteOwnAccount() – Fehler: {}", e.getMessage());
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body("Fehler beim Löschen: " + e.getMessage());
        }
    }

    // ===================== CONVERTISSEUR DTO =====================
    private UserDTO toDTO(User user) {
        return new UserDTO(
            user.getId(),
            user.getUsername(),
            user.getEmail(),
            user.getFirstname(),
            user.getLastname(),
            user.getStreet(),
            user.getStreetNr(),
            user.getZip(),
            user.getCity()
        );
    }
}
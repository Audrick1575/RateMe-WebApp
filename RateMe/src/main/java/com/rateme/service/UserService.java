package com.rateme.service;

import com.rateme.dao.UserDao;
import com.rateme.dao.RatingDao;
import com.rateme.dao.ImageDao;
import com.rateme.entity.User;
import com.rateme.entity.Rating;
import com.rateme.dto.LoginRequest;
import com.rateme.dto.RegisterRequest;
import jakarta.servlet.http.HttpSession;
import org.springframework.stereotype.Service;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.security.SecureRandom;
import java.util.List;
import java.util.Optional;

@Service
public class UserService {

    private static final Logger logger = LoggerFactory.getLogger(UserService.class);

    private final UserDao userDao;
    private final RatingDao ratingDao;
    private final ImageDao imageDao;

    // Constructeur avec injection des dépendances
    public UserService(UserDao userDao, RatingDao ratingDao, ImageDao imageDao) {
        this.userDao = userDao;
        this.ratingDao = ratingDao;
        this.imageDao = imageDao;
    }

    // ===================== INSCRIPTION =====================
    public User register(RegisterRequest request) throws Exception {
        if (userDao.findByUsername(request.username()).isPresent()) {
            throw new Exception("Username already exists");
        }
        byte[] salt = generateSalt();
        byte[] hash = hashPassword(request.password(), salt);
        User user = new User();
        user.setUsername(request.username());
        user.setEmail(request.email());
        user.setFirstname(request.firstname());
        user.setLastname(request.lastname());
        user.setStreet(request.street());
        user.setStreetNr(request.streetNr());
        user.setZip(request.zip());
        user.setCity(request.city());
        user.setPasswordSalt(salt);
        user.setPasswordHash(hash);
        logger.info("register() – Benutzername: {}", request.username());
        return userDao.save(user);
    }

    // ===================== CONNEXION =====================
    public User login(LoginRequest request, HttpSession session) throws Exception {
        Optional<User> optUser = userDao.findByUsername(request.username());
        if (optUser.isEmpty()) {
            throw new Exception("Invalid credentials");
        }
        User user = optUser.get();
        byte[] hash = hashPassword(request.password(), user.getPasswordSalt());
        if (!MessageDigest.isEqual(hash, user.getPasswordHash())) {
            throw new Exception("Invalid credentials");
        }
        session.setAttribute("user", user);
        logger.info("login() – Benutzername: {}", request.username());
        return user;
    }

    // ===================== DÉCONNEXION =====================
    public void logout(HttpSession session) {
        session.invalidate();
        logger.info("logout() – Benutzer abgemeldet");
    }

    // ===================== RÉCUPÉRER L'UTILISATEUR CONNECTÉ =====================
    public User getCurrentUser(HttpSession session) {
        return (User) session.getAttribute("user");
    }

    // ===================== SUPPRESSION DU COMPTE (BONUS 4) =====================
    public void deleteUser(User user) {
        // 1. Récupérer tous les avis de l'utilisateur
        List<Rating> ratings = ratingDao.findByUserId(user.getId());
        for (Rating rating : ratings) {
            // 2. Supprimer l'image associée (si elle existe)
            if (rating.getImage() != null) {
                imageDao.delete(rating.getImage());
            }
            // 3. Supprimer l'avis
            ratingDao.delete(rating);
        }
        // 4. Supprimer l'utilisateur
        userDao.delete(user);
        logger.info("deleteUser() – Benutzer {} und alle zugehörigen Bewertungen gelöscht", user.getUsername());
    }

    // ===================== MÉTHODES UTILITAIRES =====================
    private byte[] generateSalt() {
        SecureRandom random = new SecureRandom();
        byte[] salt = new byte[16];
        random.nextBytes(salt);
        return salt;
    }

    private byte[] hashPassword(String password, byte[] salt) throws NoSuchAlgorithmException {
        MessageDigest md = MessageDigest.getInstance("SHA-256");
        md.update(salt);
        return md.digest(password.getBytes());
    }
}
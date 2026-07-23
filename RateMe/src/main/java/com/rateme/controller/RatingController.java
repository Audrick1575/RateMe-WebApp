package com.rateme.controller;

import com.rateme.dto.CreateRatingRequest;
import com.rateme.dto.RatingDTO;
import com.rateme.dto.UpdateRatingRequest;
import com.rateme.entity.User;
import com.rateme.service.RatingService;
import com.rateme.service.UserService;
import jakarta.servlet.http.HttpSession;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.List;

@RestController
@RequestMapping("/api")
@CrossOrigin(originPatterns = "*", allowCredentials = "true")
public class RatingController {

    private static final Logger logger = LoggerFactory.getLogger(RatingController.class);
    private final RatingService ratingService;
    private final UserService userService;

    public RatingController(RatingService ratingService, UserService userService) {
        this.ratingService = ratingService;
        this.userService = userService;
    }

    // ===================== CRÉER UN AVIS =====================
    @PostMapping("/ratings")
    public ResponseEntity<?> createRating(@RequestBody CreateRatingRequest request, HttpSession session) {
        User user = userService.getCurrentUser(session);
        if (user == null) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("Not logged in");
        }
        try {
            return ResponseEntity.ok(ratingService.createRating(user, request));
        } catch (Exception e) {
            logger.error("createRating() – Fehler: {}", e.getMessage());
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(e.getMessage());
        }
    }

    // ===================== AVIS D'UN POI =====================
    @GetMapping("/ratings/poi/{poiId}")
    public List<RatingDTO> getRatingsByPoi(@PathVariable Long poiId) {
        return ratingService.getRatingsByPoi(poiId);
    }

    // ===================== MES AVIS =====================
    @GetMapping("/ratings/me")
    public ResponseEntity<?> getMyRatings(HttpSession session) {
        User user = userService.getCurrentUser(session);
        if (user == null) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("Not logged in");
        }
        return ResponseEntity.ok(ratingService.getRatingsByUser(user.getId()));
    }

    // ===================== SUPPRIMER UN AVIS (Bonus 5) =====================
    @DeleteMapping("/ratings/{id}")
    public ResponseEntity<?> deleteRating(@PathVariable Integer id, HttpSession session) {
        User user = userService.getCurrentUser(session);
        if (user == null) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("Not logged in");
        }
        try {
            ratingService.deleteRating(id, user);
            return ResponseEntity.ok("Bewertung erfolgreich gelöscht");
        } catch (Exception e) {
            logger.error("deleteRating() – Fehler: {}", e.getMessage());
            return ResponseEntity.status(HttpStatus.FORBIDDEN).body(e.getMessage());
        }
    }

    // ===================== MODIFIER UN AVIS (Bonus 5) =====================
    @PutMapping("/ratings/{id}")
    public ResponseEntity<?> updateRating(@PathVariable Integer id,
                                          @RequestBody UpdateRatingRequest request,
                                          HttpSession session) {
        User user = userService.getCurrentUser(session);
        if (user == null) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("Not logged in");
        }
        try {
            ratingService.updateRating(id, request, user);
            return ResponseEntity.ok("Bewertung erfolgreich aktualisiert");
        } catch (Exception e) {
            logger.error("updateRating() – Fehler: {}", e.getMessage());
            return ResponseEntity.status(HttpStatus.FORBIDDEN).body(e.getMessage());
        }
    }
}
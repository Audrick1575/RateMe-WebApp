package com.rateme.service;

import com.rateme.dao.RatingDao;
import com.rateme.dao.PoiDao;
import com.rateme.dao.UserDao;
import com.rateme.dao.ImageDao;
import com.rateme.dto.RatingDTO;
import com.rateme.dto.CreateRatingRequest;
import com.rateme.dto.UpdateRatingRequest;   // Nouveau DTO (on va le créer)
import com.rateme.entity.Rating;
import com.rateme.entity.User;
import com.rateme.entity.Poi;
import org.springframework.stereotype.Service;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

@Service
public class RatingService {

    private static final Logger logger = LoggerFactory.getLogger(RatingService.class);

    private final RatingDao ratingDao;
    private final PoiDao poiDao;
    private final UserDao userDao;
    private final ImageDao imageDao;   // Pour supprimer l'image associée

    public RatingService(RatingDao ratingDao, PoiDao poiDao, UserDao userDao, ImageDao imageDao) {
        this.ratingDao = ratingDao;
        this.poiDao = poiDao;
        this.userDao = userDao;
        this.imageDao = imageDao;
    }

    // ===================== CRÉER UN AVIS =====================
    public Rating createRating(User user, CreateRatingRequest request) {
        Poi poi = poiDao.findById(request.poiId()).orElseThrow();
        Rating rating = new Rating();
        rating.setUser(user);
        rating.setPoi(poi);
        rating.setGrade(request.grade());
        rating.setTxt(request.txt());
        rating.setCreatedAt(LocalDateTime.now());
        // Si imageId est fourni, on le lie (mais on le fera via upload séparé)
        logger.info("createRating() – Benutzer {} bewertet POI {}", user.getUsername(), poi.getId());
        return ratingDao.save(rating);
    }

    // ===================== RÉCUPÉRER LES AVIS D'UN UTILISATEUR =====================
    public List<RatingDTO> getRatingsByUser(Integer userId) {
        return ratingDao.findByUserId(userId).stream()
            .map(this::toDTO)
            .collect(Collectors.toList());
    }

    // ===================== RÉCUPÉRER LES AVIS D'UN POI =====================
    public List<RatingDTO> getRatingsByPoi(Long poiId) {
        return ratingDao.findByPoiId(poiId).stream()
            .map(this::toDTO)
            .collect(Collectors.toList());
    }

    // ===================== SUPPRIMER UN AVIS (Bonus 5) =====================
    public void deleteRating(Integer ratingId, User currentUser) throws Exception {
        Rating rating = ratingDao.findById(ratingId)
            .orElseThrow(() -> new Exception("Rating not found"));
        if (!rating.getUser().getId().equals(currentUser.getId())) {
            throw new Exception("You can only delete your own ratings");
        }
        // Supprimer l'image associée si elle existe
        if (rating.getImage() != null) {
            imageDao.delete(rating.getImage());
        }
        ratingDao.delete(rating);
        logger.info("deleteRating() – Bewertung {} von Benutzer {} gelöscht", ratingId, currentUser.getUsername());
    }

    // ===================== MODIFIER UN AVIS (Bonus 5) =====================
    public Rating updateRating(Integer ratingId, UpdateRatingRequest request, User currentUser) throws Exception {
        Rating rating = ratingDao.findById(ratingId)
            .orElseThrow(() -> new Exception("Rating not found"));
        if (!rating.getUser().getId().equals(currentUser.getId())) {
            throw new Exception("You can only edit your own ratings");
        }
        rating.setGrade(request.grade());
        rating.setTxt(request.txt());
        // La date de création reste inchangée
        Rating updated = ratingDao.save(rating);
        logger.info("updateRating() – Bewertung {} von Benutzer {} aktualisiert", ratingId, currentUser.getUsername());
        return updated;
    }

    // ===================== NOTE MOYENNE =====================
    public Double getAverageGrade(Long poiId) {
        return ratingDao.getAverageGradeForPoi(poiId);
    }

    // ===================== CONVERTISSEUR DTO =====================
    private RatingDTO toDTO(Rating r) {
        return new RatingDTO(
            r.getId(),
            r.getUser().getId(),
            r.getUser().getUsername(),
            r.getPoi().getId(),
            r.getPoi().getName(),
            r.getGrade(),
            r.getTxt(),
            r.getImage() != null ? r.getImage().getId() : null,
            r.getCreatedAt() != null ? r.getCreatedAt().toString() : null
        );
    }
}
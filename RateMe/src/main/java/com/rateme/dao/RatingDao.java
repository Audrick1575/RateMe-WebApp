package com.rateme.dao;

import com.rateme.entity.Rating;
import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import jakarta.persistence.TypedQuery;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;

@Repository
@Transactional
public class RatingDao {

    @PersistenceContext
    private EntityManager em;

    public Rating save(Rating rating) {
        if (rating.getId() == null) {
            em.persist(rating);
            return rating;
        } else {
            return em.merge(rating);
        }
    }

    public Optional<Rating> findById(Integer id) {
        return Optional.ofNullable(em.find(Rating.class, id));
    }

    public List<Rating> findByUserId(Integer userId) {
        TypedQuery<Rating> query = em.createQuery("SELECT r FROM Rating r WHERE r.user.id = :userId", Rating.class);
        query.setParameter("userId", userId);
        return query.getResultList();
    }

    public List<Rating> findByPoiId(Long poiId) {
        TypedQuery<Rating> query = em.createQuery("SELECT r FROM Rating r WHERE r.poi.id = :poiId", Rating.class);
        query.setParameter("poiId", poiId);
        return query.getResultList();
    }

    public List<Rating> findAll() {
        return em.createQuery("SELECT r FROM Rating r", Rating.class).getResultList();
    }

    public void delete(Rating rating) {
        em.remove(em.contains(rating) ? rating : em.merge(rating));
    }

    public Double getAverageGradeForPoi(Long poiId) {
        TypedQuery<Double> query = em.createQuery("SELECT AVG(r.grade) FROM Rating r WHERE r.poi.id = :poiId", Double.class);
        query.setParameter("poiId", poiId);
        return query.getSingleResult();
    }
}
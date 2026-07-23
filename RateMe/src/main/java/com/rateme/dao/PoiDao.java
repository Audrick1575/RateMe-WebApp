package com.rateme.dao;

import com.rateme.entity.Poi;
import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import jakarta.persistence.TypedQuery;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;

@Repository
@Transactional
public class PoiDao {

    @PersistenceContext
    private EntityManager em;

    public Optional<Poi> findById(Long id) {
        return Optional.ofNullable(em.find(Poi.class, id));
    }

    public List<Poi> findAll() {
        return em.createQuery("SELECT p FROM Poi p", Poi.class).getResultList();
    }

    public List<Poi> findByAmenity(String amenity) {
        TypedQuery<Poi> query = em.createQuery("SELECT p FROM Poi p WHERE p.amenity = :amenity", Poi.class);
        query.setParameter("amenity", amenity);
        return query.getResultList();
    }
}
package com.rateme.dao;

import com.rateme.entity.Image;
import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import java.util.Optional;

@Repository
@Transactional
public class ImageDao {

    @PersistenceContext
    private EntityManager em;

    public Image save(Image image) {
        if (image.getId() == null) {
            em.persist(image);
            return image;
        } else {
            return em.merge(image);
        }
    }

    public Optional<Image> findById(Integer id) {
        return Optional.ofNullable(em.find(Image.class, id));
    }

    public void delete(Image image) {
        em.remove(em.contains(image) ? image : em.merge(image));
    }
}
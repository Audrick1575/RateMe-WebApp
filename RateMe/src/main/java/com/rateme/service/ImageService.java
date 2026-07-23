package com.rateme.service;

import com.rateme.dao.ImageDao;
import com.rateme.entity.Image;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Service
public class ImageService {

    private final ImageDao imageDao;

    public ImageService(ImageDao imageDao) {
        this.imageDao = imageDao;
    }

    public Image saveImage(byte[] data) {
        Image image = new Image();
        image.setImg(data);
        return imageDao.save(image);
    }

    public Optional<Image> getImage(Integer id) {
        return imageDao.findById(id);
    }

    public void deleteImage(Integer id) {
        imageDao.findById(id).ifPresent(imageDao::delete);
    }
}
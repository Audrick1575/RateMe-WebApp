package com.rateme.service;

import com.rateme.dao.PoiDao;
import com.rateme.entity.Poi;
import com.rateme.dto.PoiDTO;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

@Service
public class PoiService {

    private final PoiDao poiDao;

    public PoiService(PoiDao poiDao) {
        this.poiDao = poiDao;
    }

    public List<PoiDTO> getAllPois() {
        return poiDao.findAll().stream()
            .map(this::toDTO)
            .collect(Collectors.toList());
    }

    public PoiDTO getPoiById(Long id) {
        return poiDao.findById(id)
            .map(this::toDTO)
            .orElse(null);
    }

    public Poi getPoiEntity(Long id) {
        return poiDao.findById(id).orElse(null);
    }

    private PoiDTO toDTO(Poi p) {
        return new PoiDTO(
            p.getId(),
            p.getName(),
            p.getLat(),
            p.getLon(),
            p.getAmenity(),
            p.getCuisine(),
            p.getPhone(),
            p.getOpeningHours(),
            p.getWebsite(),
            p.getWheelchair(),
            p.getTakeaway(),
            p.getDelivery(),
            p.getSmoking(),
            p.getOutdoorSeating(),
            p.getReservation(),
            p.getAddrCity(),
            p.getAddrStreet(),
            p.getAddrHousenumber(),
            p.getAddrPostcode()
        );
    }
}

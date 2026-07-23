package com.rateme.dto;

public record PoiDTO(
    Long id,
    String name,
    Double lat,
    Double lon,
    String amenity,
    String cuisine,
    String phone,
    String openingHours,
    String website,
    String wheelchair,
    String takeaway,
    String delivery,
    String smoking,
    String outdoorSeating,
    String reservation,
    String addrCity,
    String addrStreet,
    String addrHousenumber,
    String addrPostcode
) {}
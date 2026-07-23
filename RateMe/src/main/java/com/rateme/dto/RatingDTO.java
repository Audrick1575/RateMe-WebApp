package com.rateme.dto;

public record RatingDTO(
    Integer id,
    Integer userId,
    String username,
    Long poiId,
    String poiName,
    Integer grade,
    String txt,
    Integer imageId,
    String createdAt
) {}

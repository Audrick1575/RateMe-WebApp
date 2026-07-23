package com.rateme.dto;

public record CreateRatingRequest(
    Long poiId,
    Integer grade,
    String txt,
    Integer imageId // optionnel
) {}
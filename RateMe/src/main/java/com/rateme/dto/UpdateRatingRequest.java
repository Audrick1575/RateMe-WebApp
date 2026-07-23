package com.rateme.dto;

public record UpdateRatingRequest(
    Integer grade,
    String txt
) {}
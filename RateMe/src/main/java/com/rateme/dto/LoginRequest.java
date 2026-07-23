package com.rateme.dto;

public record LoginRequest(
    String username,
    String password
) {}
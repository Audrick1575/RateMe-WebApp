package com.rateme.dto;

public record RegisterRequest(
    String username,
    String email,
    String firstname,
    String lastname,
    String street,
    String streetNr,
    String zip,
    String city,
    String password
) {}

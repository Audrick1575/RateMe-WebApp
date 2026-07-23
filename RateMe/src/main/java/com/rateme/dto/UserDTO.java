package com.rateme.dto;

public record UserDTO(
    Integer id,
    String username,
    String email,
    String firstname,
    String lastname,
    String street,
    String streetNr,
    String zip,
    String city
) {}
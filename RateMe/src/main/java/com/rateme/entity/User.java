package com.rateme.entity;

import jakarta.persistence.*;

@Entity
@Table(name = "user")
public class User {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    @Column(length = 20, nullable = false, unique = true)
    private String username;

    @Column(length = 50, nullable = false)
    private String email;

    @Column(length = 20, nullable = false)
    private String firstname;

    @Column(length = 20, nullable = false)
    private String lastname;

    @Column(length = 30, nullable = false)
    private String street;

    @Column(name = "street_nr", length = 20, nullable = false)
    private String streetNr;

    @Column(length = 20, nullable = false)
    private String zip;

    @Column(length = 30, nullable = false)
    private String city;

    @Column(name = "password_hash", columnDefinition = "VARBINARY(1000)", nullable = false)
    private byte[] passwordHash;

    @Column(name = "password_salt", columnDefinition = "VARBINARY(1000)", nullable = false)
    private byte[] passwordSalt;

    public User() {}

    // Getters et Setters
    public Integer getId() { return id; }
    public void setId(Integer id) { this.id = id; }
    public String getUsername() { return username; }
    public void setUsername(String username) { this.username = username; }
    public String getEmail() { return email; }
    public void setEmail(String email) { this.email = email; }
    public String getFirstname() { return firstname; }
    public void setFirstname(String firstname) { this.firstname = firstname; }
    public String getLastname() { return lastname; }
    public void setLastname(String lastname) { this.lastname = lastname; }
    public String getStreet() { return street; }
    public void setStreet(String street) { this.street = street; }
    public String getStreetNr() { return streetNr; }
    public void setStreetNr(String streetNr) { this.streetNr = streetNr; }
    public String getZip() { return zip; }
    public void setZip(String zip) { this.zip = zip; }
    public String getCity() { return city; }
    public void setCity(String city) { this.city = city; }
    public byte[] getPasswordHash() { return passwordHash; }
    public void setPasswordHash(byte[] passwordHash) { this.passwordHash = passwordHash; }
    public byte[] getPasswordSalt() { return passwordSalt; }
    public void setPasswordSalt(byte[] passwordSalt) { this.passwordSalt = passwordSalt; }
}
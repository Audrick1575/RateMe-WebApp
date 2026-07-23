package com.rateme.entity;

import jakarta.persistence.*;

@Entity
@Table(name = "poi")
public class Poi {

    @Id
    private Long id;

    @Column(columnDefinition = "ENUM('node','way','relation')", nullable = false)
    private String type;

    private Double lat;
    private Double lon;
    private String name;
    private String amenity;
    private String cuisine;
    private String phone;

    @Column(name = "opening_hours")
    private String openingHours;

    private String website;
    private String wheelchair;
    private String takeaway;
    private String delivery;
    private String smoking;

    @Column(name = "outdoor_seating")
    private String outdoorSeating;

    private String reservation;

    @Column(name = "addr_city")
    private String addrCity;

    @Column(name = "addr_country")
    private String addrCountry;

    @Column(name = "addr_housenumber")
    private String addrHousenumber;

    @Column(name = "addr_postcode")
    private String addrPostcode;

    @Column(name = "addr_street")
    private String addrStreet;

    @Column(columnDefinition = "JSON NOT NULL")
    private String tags;

    // Constructeur par défaut
    public Poi() {}

    // Getters et Setters (à générer)
    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }
    public String getType() { return type; }
    public void setType(String type) { this.type = type; }
    public Double getLat() { return lat; }
    public void setLat(Double lat) { this.lat = lat; }
    public Double getLon() { return lon; }
    public void setLon(Double lon) { this.lon = lon; }
    public String getName() { return name; }
    public void setName(String name) { this.name = name; }
    public String getAmenity() { return amenity; }
    public void setAmenity(String amenity) { this.amenity = amenity; }
    public String getCuisine() { return cuisine; }
    public void setCuisine(String cuisine) { this.cuisine = cuisine; }
    public String getPhone() { return phone; }
    public void setPhone(String phone) { this.phone = phone; }
    public String getOpeningHours() { return openingHours; }
    public void setOpeningHours(String openingHours) { this.openingHours = openingHours; }
    public String getWebsite() { return website; }
    public void setWebsite(String website) { this.website = website; }
    public String getWheelchair() { return wheelchair; }
    public void setWheelchair(String wheelchair) { this.wheelchair = wheelchair; }
    public String getTakeaway() { return takeaway; }
    public void setTakeaway(String takeaway) { this.takeaway = takeaway; }
    public String getDelivery() { return delivery; }
    public void setDelivery(String delivery) { this.delivery = delivery; }
    public String getSmoking() { return smoking; }
    public void setSmoking(String smoking) { this.smoking = smoking; }
    public String getOutdoorSeating() { return outdoorSeating; }
    public void setOutdoorSeating(String outdoorSeating) { this.outdoorSeating = outdoorSeating; }
    public String getReservation() { return reservation; }
    public void setReservation(String reservation) { this.reservation = reservation; }
    public String getAddrCity() { return addrCity; }
    public void setAddrCity(String addrCity) { this.addrCity = addrCity; }
    public String getAddrCountry() { return addrCountry; }
    public void setAddrCountry(String addrCountry) { this.addrCountry = addrCountry; }
    public String getAddrHousenumber() { return addrHousenumber; }
    public void setAddrHousenumber(String addrHousenumber) { this.addrHousenumber = addrHousenumber; }
    public String getAddrPostcode() { return addrPostcode; }
    public void setAddrPostcode(String addrPostcode) { this.addrPostcode = addrPostcode; }
    public String getAddrStreet() { return addrStreet; }
    public void setAddrStreet(String addrStreet) { this.addrStreet = addrStreet; }
    public String getTags() { return tags; }
    public void setTags(String tags) { this.tags = tags; }
}
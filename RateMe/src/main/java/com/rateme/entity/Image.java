package com.rateme.entity;

import jakarta.persistence.*;

@Entity
@Table(name = "image")
public class Image {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    @Lob
    @Column(columnDefinition = "MEDIUMBLOB", nullable = false)
    private byte[] img;

    public Image() {}

    public Integer getId() { return id; }
    public void setId(Integer id) { this.id = id; }
    public byte[] getImg() { return img; }
    public void setImg(byte[] img) { this.img = img; }
}
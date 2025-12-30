package com.bac.backend.model;

import jakarta.persistence.*;
import lombok.Data;

@Entity
@Data
@Table(name = "konsultan")
public class Konsultan {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String nama;
    private String job;
    
    @Column(columnDefinition = "TEXT")
    private String deskripsi;
    
    private String linkWa;
    private String fotoProfile;
}
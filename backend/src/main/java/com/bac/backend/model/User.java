package com.bac.backend.model;

import jakarta.persistence.*;
import lombok.Data;

@Entity
@Data
@Table(name = "users")
public class User {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private String nama;

    @Column(nullable = false)
    private String role; 

    @Column(unique = true, nullable = false)
    private String nim;

    @Column(nullable = false)
    private String password;
    
    private String fotoProfile;
    // --- TAMBAHAN BARU ---
    @Column(name = "jurusan")
    private String jurusan;
}
package com.bac.backend.model;

import jakarta.persistence.*;
import lombok.Data;
import java.time.LocalDateTime;

@Entity
@Data
@Table(name = "laporan")
public class Laporan {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    // Data Pelapor (Otomatis dari Sistem)
    private String namaPelapor;
    private String nimPelapor;

    // Data Laporan
    
    // TAMBAHKAN INI (Sesuai nama kolom di bac.sql: judul_laporan)
    @Column(name = "judul_laporan") 
    private String judulLaporan;

    private String programStudi;
    private String kategori;
    private String tempatKejadian;
    
    private LocalDateTime waktuKejadian; 

    @Column(columnDefinition = "TEXT")
    private String deskripsi;

    // Nama file bukti yang diupload
    private String buktiFoto; 

    // Privasi
    private boolean anonim; 

    // Status Laporan (Diterima, Diproses, Selesai)
    private String status; 
}
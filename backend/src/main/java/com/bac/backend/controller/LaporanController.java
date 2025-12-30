package com.bac.backend.controller;

import org.springframework.core.io.Resource;
import org.springframework.core.io.UrlResource;
import org.springframework.http.HttpHeaders;
import com.bac.backend.model.Laporan;
import com.bac.backend.repository.LaporanRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.List;

@RestController
@RequestMapping("/api/laporan")
@CrossOrigin(origins = "*")
public class LaporanController {

    @Autowired
    private LaporanRepository laporanRepository;

    private static final String UPLOAD_DIR = "uploads/";

    @PostMapping("/create")
    public ResponseEntity<?> buatLaporan(
            @RequestParam("nama") String nama,
            @RequestParam("nim") String nim,
            @RequestParam("prodi") String prodi,
            @RequestParam("judul") String judul, // <--- TAMBAHKAN PARAM INI
            @RequestParam("kategori") String kategori,
            @RequestParam("tempat") String tempat,
            @RequestParam("waktu") String waktuString, 
            @RequestParam("deskripsi") String deskripsi,
            @RequestParam(value = "anonim", required = false) boolean anonim,
            @RequestParam(value = "bukti", required = false) MultipartFile file
    ) {
        try {
            Laporan laporan = new Laporan();
            laporan.setNamaPelapor(nama);
            laporan.setNimPelapor(nim);
            laporan.setProgramStudi(prodi);
            
            laporan.setJudulLaporan(judul); // <--- SIMPAN JUDUL KE SINI
            
            laporan.setKategori(kategori);
            laporan.setTempatKejadian(tempat);
            laporan.setDeskripsi(deskripsi);
            laporan.setAnonim(anonim);
            
            // SET STATUS AWAL: DITERIMA
            laporan.setStatus("Diterima");

            // Convert String waktu dari HTML ke LocalDateTime Java
            DateTimeFormatter formatter = DateTimeFormatter.ISO_LOCAL_DATE_TIME;
            laporan.setWaktuKejadian(LocalDateTime.parse(waktuString, formatter));

            // HANDLE FILE UPLOAD (Jika ada file)
            if (file != null && !file.isEmpty()) {
                Path uploadPath = Paths.get(UPLOAD_DIR);
                if (!Files.exists(uploadPath)) {
                    Files.createDirectories(uploadPath);
                }
                String fileName = System.currentTimeMillis() + "_" + file.getOriginalFilename();
                Path filePath = uploadPath.resolve(fileName);
                Files.copy(file.getInputStream(), filePath);

                laporan.setBuktiFoto(fileName); 
            }

            laporanRepository.save(laporan);
            return ResponseEntity.ok("Laporan berhasil dikirim!");

        } catch (IOException e) {
            return ResponseEntity.internalServerError().body("Gagal upload file: " + e.getMessage());
        } catch (Exception e) {
            return ResponseEntity.badRequest().body("Terjadi kesalahan: " + e.getMessage());
        }
    }

    // Endpoint Riwayat Laporan (Tetap sama, tidak perlu diubah)
    @GetMapping("/history/{nim}")
    public ResponseEntity<?> getRiwayatLaporan(@PathVariable String nim) {
        List<Laporan> riwayat = laporanRepository.findByNimPelapor(nim);
        if (riwayat.isEmpty()) {
            return ResponseEntity.ok(List.of()); 
        }
        return ResponseEntity.ok(riwayat);
    }
    // --- 3. API KHUSUS UNTUK MENAMPILKAN GAMBAR/VIDEO ---
    @GetMapping("/file/{filename:.+}")
    public ResponseEntity<Resource> getFile(@PathVariable String filename) {
        try {
            Path file = Paths.get(UPLOAD_DIR).resolve(filename);
            Resource resource = new UrlResource(file.toUri());

            if (resource.exists() || resource.isReadable()) {
                // Coba tentukan tipe konten (image/jpeg, video/mp4, dll)
                String contentType = Files.probeContentType(file);
                if (contentType == null) {
                    contentType = "application/octet-stream";
                }

                return ResponseEntity.ok()
                        .header(HttpHeaders.CONTENT_TYPE, contentType)
                        .body(resource);
            } else {
                return ResponseEntity.notFound().build();
            }
        } catch (Exception e) {
            return ResponseEntity.badRequest().build();
        }
    }
    @GetMapping("/all")
    public ResponseEntity<?> getAllLaporan() {
        // Mengambil semua data dari tabel laporan, diurutkan dari yang terbaru (ID DESC)
        List<Laporan> allLaporan = laporanRepository.findAll(org.springframework.data.domain.Sort.by(org.springframework.data.domain.Sort.Direction.DESC, "id"));
        return ResponseEntity.ok(allLaporan);
    }

    // --- 5. API UNTUK ADMIN: UPDATE STATUS LAPORAN ---
    @PutMapping("/update-status/{id}")
    public ResponseEntity<?> updateStatusLaporan(@PathVariable Long id, @RequestBody java.util.Map<String, String> payload) {
        String newStatus = payload.get("status");
        
        return laporanRepository.findById(id).map(laporan -> {
            laporan.setStatus(newStatus);
            laporanRepository.save(laporan);
            return ResponseEntity.ok("Status berhasil diperbarui menjadi: " + newStatus);
        }).orElse(ResponseEntity.notFound().build());
    }
}
package com.bac.backend.controller;

import com.bac.backend.model.Konsultan;
import com.bac.backend.repository.KonsultanRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.core.io.Resource;
import org.springframework.core.io.UrlResource;
import org.springframework.http.HttpHeaders;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.List;

@RestController
@RequestMapping("/api/konsultan")
@CrossOrigin(origins = "*")
public class KonsultanController {

    @Autowired
    private KonsultanRepository konsultanRepository;

    private static final String UPLOAD_DIR = "uploads/";

    // 1. AMBIL SEMUA KONSULTAN
    @GetMapping("/all")
    public ResponseEntity<?> getAllKonsultan() {
        return ResponseEntity.ok(konsultanRepository.findAll());
    }

    // 2. TAMBAH KONSULTAN BARU (DENGAN FOTO)
    @PostMapping("/create")
    public ResponseEntity<?> createKonsultan(
            @RequestParam("nama") String nama,
            @RequestParam("job") String job,
            @RequestParam("deskripsi") String deskripsi,
            @RequestParam("linkWa") String linkWa,
            @RequestParam(value = "foto", required = false) MultipartFile file
    ) {
        try {
            Konsultan k = new Konsultan();
            k.setNama(nama);
            k.setJob(job);
            k.setDeskripsi(deskripsi);
            k.setLinkWa(linkWa);

            // Upload Foto
            if (file != null && !file.isEmpty()) {
                Path uploadPath = Paths.get(UPLOAD_DIR);
                if (!Files.exists(uploadPath)) Files.createDirectories(uploadPath);

                String fileName = System.currentTimeMillis() + "_konsultan_" + file.getOriginalFilename();
                Path filePath = uploadPath.resolve(fileName);
                Files.copy(file.getInputStream(), filePath);
                k.setFotoProfile(fileName);
            }

            konsultanRepository.save(k);
            return ResponseEntity.ok("Konsultan berhasil ditambahkan!");

        } catch (IOException e) {
            return ResponseEntity.internalServerError().body("Gagal upload file.");
        }
    }

    // 3. AKSES FOTO PROFIL
    @GetMapping("/foto/{filename:.+}")
    public ResponseEntity<Resource> getFoto(@PathVariable String filename) {
        try {
            Path file = Paths.get(UPLOAD_DIR).resolve(filename);
            Resource resource = new UrlResource(file.toUri());
            if (resource.exists() || resource.isReadable()) {
                return ResponseEntity.ok().body(resource);
            } else {
                return ResponseEntity.notFound().build();
            }
        } catch (Exception e) {
            return ResponseEntity.badRequest().build();
        }
    }

    // 4. HAPUS KONSULTAN (INI YANG TADI HILANG)
    @DeleteMapping("/delete/{id}")
    public ResponseEntity<?> deleteKonsultan(@PathVariable Long id) {
        if (konsultanRepository.existsById(id)) {
            konsultanRepository.deleteById(id);
            return ResponseEntity.ok("Konsultan berhasil dihapus!");
        } else {
            return ResponseEntity.notFound().build();
        }
    }
}
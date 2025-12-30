package com.bac.backend.controller;

import com.bac.backend.model.User;
import com.bac.backend.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.Resource;
import org.springframework.core.io.UrlResource;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.net.MalformedURLException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/api/users")
@CrossOrigin(origins = "*")
public class UserController {

    @Autowired
    private UserRepository userRepository;

    // Folder penyimpanan foto
    private static final String UPLOAD_DIR = "uploads/";

    // 1. Ambil Data User berdasarkan NIM
    @GetMapping("/{nim}")
    public ResponseEntity<?> getUserByNim(@PathVariable String nim) {
        Optional<User> user = userRepository.findByNim(nim);
        if (user.isPresent()) {
            return ResponseEntity.ok(user.get());
        }
        return ResponseEntity.notFound().build();
    }

    // 2. Update Data User (Nama, Password, & Foto Profil)
    @PutMapping("/{nim}")
    public ResponseEntity<?> updateUser(
            @PathVariable String nim,
            @RequestParam("nama") String nama,
            @RequestParam(value = "password", required = false) String password,
            @RequestParam(value = "foto", required = false) MultipartFile file
    ) {
        Optional<User> userOpt = userRepository.findByNim(nim);

        if (userOpt.isPresent()) {
            User existingUser = userOpt.get();

            // Update Nama
            if (nama != null && !nama.isEmpty()) {
                existingUser.setNama(nama);
            }

            // Update Password
            if (password != null && !password.isEmpty()) {
                existingUser.setPassword(password);
            }

            // Update Foto Profil
            if (file != null && !file.isEmpty()) {
                try {
                    Path uploadPath = Paths.get(UPLOAD_DIR);
                    if (!Files.exists(uploadPath)) {
                        Files.createDirectories(uploadPath);
                    }

                    String fileName = System.currentTimeMillis() + "_profile_" + file.getOriginalFilename();
                    Path filePath = uploadPath.resolve(fileName);
                    Files.copy(file.getInputStream(), filePath);

                    existingUser.setFotoProfile(fileName);

                } catch (IOException e) {
                    return ResponseEntity.internalServerError().body("Gagal upload foto: " + e.getMessage());
                }
            }

            userRepository.save(existingUser);
            return ResponseEntity.ok("Profil berhasil diperbarui!");
        }
        return ResponseEntity.notFound().build();
    }

    // --- 3. (BARU) API UNTUK MENAMPILKAN FOTO PROFIL ---
    @GetMapping("/foto/{filename:.+}")
    public ResponseEntity<Resource> getFotoProfil(@PathVariable String filename) {
        try {
            Path file = Paths.get(UPLOAD_DIR).resolve(filename);
            Resource resource = new UrlResource(file.toUri());

            if (resource.exists() || resource.isReadable()) {
                return ResponseEntity.ok().body(resource);
            } else {
                return ResponseEntity.notFound().build();
            }
        } catch (MalformedURLException e) {
            return ResponseEntity.badRequest().build();
        }
    }

    // 4. Ambil Semua Member (Untuk Admin)
    @GetMapping("/members")
    public ResponseEntity<?> getAllMembers() {
        List<User> members = userRepository.findAll().stream()
                .filter(user -> "MEMBER".equals(user.getRole()))
                .collect(Collectors.toList());

        return ResponseEntity.ok(members);
    }
}
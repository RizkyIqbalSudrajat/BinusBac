package com.bac.backend.controller;

import com.bac.backend.model.User;
import com.bac.backend.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.Map;
import java.util.Optional;

@RestController
@RequestMapping("/api")
@CrossOrigin(origins = "*") // Agar HTML bisa akses API tanpa diblokir browser
public class AuthController {

    @Autowired
    private UserRepository userRepository;

    // --- FITUR REGISTER ---
    @PostMapping("/register")
    public ResponseEntity<?> registerUser(@RequestBody User user) {
        // 1. Cek apakah NIM sudah ada
        if (userRepository.findByNim(user.getNim()).isPresent()) {
            return ResponseEntity.badRequest().body("NIM sudah terdaftar!");
        }
        
        // 2. PAKSA ROLE JADI "MEMBER" (Hardcode)
        // Apapun yang dikirim dari frontend, kita timpa jadi MEMBER agar aman.
        user.setRole("MEMBER");

        userRepository.save(user);
        return ResponseEntity.ok("Registrasi Berhasil!");
    }

    // --- FITUR LOGIN ---
    @PostMapping("/login")
    public ResponseEntity<?> loginUser(@RequestBody Map<String, String> loginData) {
        String nim = loginData.get("nim");
        String password = loginData.get("password");

        Optional<User> userOpt = userRepository.findByNim(nim);

        if (userOpt.isPresent()) {
            User user = userOpt.get();
            // Cek Password (sederhana tanpa enkripsi dulu agar mudah dipahami)
            if (user.getPassword().equals(password)) {
                // Kembalikan data user & role ke Frontend
                return ResponseEntity.ok(Map.of(
                    "message", "Login Berhasil",
                    "role", user.getRole(),
                    "nama", user.getNama()
                ));
            }
        }
        return ResponseEntity.status(401).body("NIM atau Password Salah!");
    }
}
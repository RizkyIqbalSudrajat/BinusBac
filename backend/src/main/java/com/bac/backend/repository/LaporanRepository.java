package com.bac.backend.repository;

import com.bac.backend.model.Laporan;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.List;

public interface LaporanRepository extends JpaRepository<Laporan, Long> {
    // Nanti berguna untuk menampilkan riwayat laporan berdasarkan NIM user
    List<Laporan> findByNimPelapor(String nimPelapor);
}
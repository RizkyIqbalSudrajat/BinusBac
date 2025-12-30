package com.bac.backend.repository;

import com.bac.backend.model.Konsultan;
import org.springframework.data.jpa.repository.JpaRepository;

public interface KonsultanRepository extends JpaRepository<Konsultan, Long> {
}
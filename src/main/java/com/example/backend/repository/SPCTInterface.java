package com.example.backend.repository;

import com.example.backend.entity.SanPhamChiTiet;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface SPCTInterface  extends JpaRepository<SanPhamChiTiet, Integer> {
}

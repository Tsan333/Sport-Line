package com.example.backend.repository;

import com.example.backend.entity.SanPham;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface SanPhamInterface extends JpaRepository<SanPham,Integer> {
    List<SanPham> findAllByTrangThai(int trangThai);
    Optional<SanPham> findByTenSanPhamIgnoreCase(String maSanPham);
}

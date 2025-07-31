package com.example.backend.repository;

import com.example.backend.entity.KhachHang;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface KhachHangRepository extends JpaRepository<KhachHang,Integer> {
    Optional<KhachHang> findBySoDienThoai(String soDienThoai);
    Optional<KhachHang> findByEmail(String email);
    boolean existsByEmail(String email);
    Page<KhachHang> findAll(Pageable pageable);

    @Query("SELECT kh FROM KhachHang kh WHERE " +
            "LOWER(kh.tenKhachHang) LIKE LOWER(CONCAT('%', :keyword, '%')) OR " +
            "LOWER(kh.email) LIKE LOWER(CONCAT('%', :keyword, '%')) OR " +
            "LOWER(kh.soDienThoai) LIKE LOWER(CONCAT('%', :keyword, '%'))")
    List<KhachHang> search(@Param("keyword") String keyword);
}

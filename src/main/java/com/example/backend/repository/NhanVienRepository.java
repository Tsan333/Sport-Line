package com.example.backend.repository;

import com.example.backend.entity.NhanVien;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface NhanVienRepository extends JpaRepository<NhanVien, Integer> {
    Optional<NhanVien> findByEmail(String email);

    Optional<NhanVien> findBySoDienThoai(String soDienThoai);

    Page<NhanVien> findAll(Pageable pageable);

    @Query("SELECT nv FROM NhanVien nv WHERE " +
            "LOWER(nv.tenNhanVien) LIKE LOWER(CONCAT('%', :keyword, '%')) OR " +
            "LOWER(nv.email) LIKE LOWER(CONCAT('%', :keyword, '%')) OR " +
            "LOWER(nv.soDienThoai) LIKE LOWER(CONCAT('%', :keyword, '%'))")
    List<NhanVien> search(@Param("keyword") String keyword);
}
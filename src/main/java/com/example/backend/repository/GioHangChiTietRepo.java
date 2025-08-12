package com.example.backend.repository;

import com.example.backend.dto.GioHangChiTietResponse;
import com.example.backend.entity.GioHangChiTiet;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface GioHangChiTietRepo extends JpaRepository<GioHangChiTiet,Integer> {

    @Query("SELECT COALESCE(SUM(g.soLuong),0) FROM GioHangChiTiet g WHERE g.khachHang.id = :id")
    int demTongSoLuongTrongGioKhach(@Param("id") Integer idKhach);

    @Query("SELECT COALESCE(SUM(g.soLuong * g.gia),0) FROM GioHangChiTiet g WHERE g.khachHang.id = :id")
    double tinhTongTienGioHang(@Param("id") Integer idKhach);

    GioHangChiTiet findBySanPhamChiTietIdAndKhachHangId(Integer idSpct, Integer idKhachHang);

    List<GioHangChiTiet> findByKhachHangId(Integer idKhachHang);

    // ← Query mới để lấy giỏ hàng với thông tin đầy đủ
    @Query("""
        SELECT new com.example.backend.dto.GioHangChiTietResponse(
            g.id,
            g.soLuong,
            g.gia,
            spct.id,
            spct.giaBan,
            spct.giaBanGiamGia,
            sp.tenSanPham,
            sp.imanges,
            kt.tenKichThuoc,
            ms.tenMauSac,
            sp.id
        )
        FROM GioHangChiTiet g
        JOIN g.sanPhamChiTiet spct
        JOIN spct.sanPham sp
        JOIN spct.kichThuoc kt
        JOIN spct.mauSac ms
        WHERE g.khachHang.id = :idKhachHang
    """)
    List<GioHangChiTietResponse> getDanhSachTheoKhachWithDetails(@Param("idKhachHang") Integer idKhachHang);
}
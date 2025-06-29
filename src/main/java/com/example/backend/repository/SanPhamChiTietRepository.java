package com.example.backend.repository;


import com.example.backend.dto.SPCTDTO;
import com.example.backend.dto.SPCTRequest;
import com.example.backend.dto.SanPhamDonHangResponse;
import com.example.backend.entity.SanPhamChiTiet;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

import java.util.Optional;

@Repository
public interface SanPhamChiTietRepository extends JpaRepository<SanPhamChiTiet,Integer> {

    @Query(
            """
            SELECT new com.example.backend.dto.SPCTDTO(
                sp.tenSanPham,
                spct.soLuong,
                spct.giaBan,
                kt.tenKichThuoc,
                ms.tenMauSac
            )
            FROM SanPhamChiTiet spct
            JOIN spct.sanPham sp
            JOIN spct.kichThuoc kt
            JOIN spct.mauSac ms
            """
    )
    List<SPCTDTO> getAllSPCTDTO();

    @Query(
            """
            SELECT new com.example.backend.dto.SPCTDTO(
                sp.tenSanPham,
                spct.soLuong,
                spct.giaBan,
                kt.tenKichThuoc,
                ms.tenMauSac
            )
            FROM SanPhamChiTiet spct
            JOIN spct.sanPham sp
            JOIN spct.kichThuoc kt
            JOIN spct.mauSac ms
            where spct.id = :id
            """
    )
    Optional<SPCTDTO> getSPCTDTOById(Integer id);

    @Query(
            """
            SELECT new com.example.backend.dto.SanPhamDonHangResponse(
                sp.tenSanPham,
                kt.tenKichThuoc,
                ms.tenMauSac,
                dhct.soLuong,
                dhct.thanhTien
            )
            FROM DonHangChiTiet dhct
            JOIN dhct.sanPhamChiTiet spct
            JOIN spct.sanPham sp
            JOIN spct.kichThuoc kt
            JOIN spct.mauSac ms
            WHERE dhct.donHang.id = :idDonHang
            """
    )
    List<SanPhamDonHangResponse> getSanPhamByDonHang(@Param("idDonHang") Integer idDonHang);

    @Query(
            """
            SELECT new com.example.backend.dto.SPCTDTO(
                sp.tenSanPham,
                spct.soLuong,
                spct.giaBan,
                kt.tenKichThuoc,
                ms.tenMauSac
            )
            FROM SanPhamChiTiet spct
            JOIN spct.sanPham sp
            JOIN spct.kichThuoc kt
            JOIN spct.mauSac ms
            where sp.id = :id
            """
    )
    List<SPCTDTO> getSPCTDTOByIdSP(Integer id);


    @Query(
            """
            SELECT new com.example.backend.dto.SPCTDTO(
                sp.tenSanPham,
                spct.soLuong,
                spct.giaBan,
                kt.tenKichThuoc,
                ms.tenMauSac
            )
            FROM SanPhamChiTiet spct
            JOIN spct.sanPham sp
            JOIN spct.kichThuoc kt
            JOIN spct.mauSac ms
            WHERE LOWER(sp.tenSanPham) LIKE LOWER(CONCAT('%', :keyword, '%'))
            """
    )
    List<SPCTDTO> searchByTenSanPham(@Param("keyword") String keyword);

}

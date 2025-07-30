package com.example.backend.repository;

import com.example.backend.dto.BestSellerProductDTO;
import com.example.backend.dto.DonHangChiTietDTO;
import com.example.backend.entity.DonHangChiTiet;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;

@Repository
public interface DonHangChiTietRepository extends JpaRepository<DonHangChiTiet,Integer> {

    @Modifying
    @Query("DELETE FROM DonHangChiTiet d WHERE d.donHang.id = :idDonHang")
    void deleteByDonHangId(@Param("idDonHang") Integer idDonHang);

    @Query("""
    SELECT new com.example.backend.dto.DonHangChiTietDTO(
        dhct.id,
        dhct.donHang.id,
        dhct.sanPhamChiTiet.id,
        dhct.soLuong,
        dhct.gia,
        dhct.thanhTien
    )
    FROM DonHangChiTiet dhct
    WHERE dhct.donHang.id = :id
""")
    List<DonHangChiTietDTO> findByDonHangId(@Param("id") Integer id);

    List<DonHangChiTiet> findEntityByDonHang_Id(Integer idDonHang);

    @Query("SELECT SUM(ct.soLuong) FROM DonHangChiTiet ct JOIN ct.donHang dh WHERE dh.trangThai = 4")
    Long sumTotalProductsSold();

    @Query("SELECT new com.example.backend.dto.BestSellerProductDTO(spct.id, sp.tenSanPham, th.tenThuongHieu, SUM(ct.soLuong)) " +
            "FROM DonHangChiTiet ct JOIN ct.donHang dh JOIN ct.sanPhamChiTiet spct JOIN spct.sanPham sp JOIN sp.thuongHieu th " +
            "WHERE dh.trangThai = 4 AND dh.ngayMua BETWEEN :start AND :end " +
            "GROUP BY spct.id, sp.tenSanPham, th.tenThuongHieu ORDER BY SUM(ct.soLuong) DESC")
    List<BestSellerProductDTO> findBestSellers(@Param("start") LocalDateTime start, @Param("end") LocalDateTime end);

}

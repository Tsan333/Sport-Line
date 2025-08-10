package com.example.backend.repository;

import com.example.backend.dto.BestSellerProductDTO;
import com.example.backend.dto.DonHangChiTietDTO;
import com.example.backend.entity.DonHangChiTiet;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

@Repository
public interface DonHangChiTietRepository extends JpaRepository<DonHangChiTiet,Integer> {
    List<DonHangChiTiet> findBySanPhamChiTiet_Id(Integer sanPhamChiTietId);

    @Modifying
    @Query("DELETE FROM DonHangChiTiet d WHERE d.donHang.id = :idDonHang")
    void deleteByDonHangId(@Param("idDonHang") Integer idDonHang);

    Optional<DonHangChiTiet> findByDonHang_IdAndSanPhamChiTiet_Id(Integer idDonHang, Integer idSanPhamChiTiet);

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
    JOIN dhct.sanPhamChiTiet spct
    JOIN spct.sanPham sp
    WHERE dhct.donHang.id = :id
    AND spct.trangThai = 1
    AND sp.trangThai = 1
    
""")
    List<DonHangChiTietDTO> findByDonHangId(@Param("id") Integer id);

    List<DonHangChiTiet> findByDonHang_Id(Integer donHangId);

    @Query("SELECT SUM(ct.soLuong) FROM DonHangChiTiet ct JOIN ct.donHang dh WHERE dh.trangThai = 4")
    Long sumTotalProductsSold();

//    @Query("SELECT new com.example.backend.dto.BestSellerProductDTO(spct.id, sp.tenSanPham, th.tenThuongHieu, SUM(ct.soLuong)) " +
//            "FROM DonHangChiTiet ct JOIN ct.donHang dh JOIN ct.sanPhamChiTiet spct JOIN spct.sanPham sp JOIN sp.thuongHieu th " +
//            "WHERE dh.trangThai = 4 AND dh.ngayMua BETWEEN :start AND :end " +
//            "GROUP BY spct.id, sp.tenSanPham, th.tenThuongHieu ORDER BY SUM(ct.soLuong) DESC")
//    List<BestSellerProductDTO> findBestSellers(@Param("start") LocalDate start, @Param("end") LocalDate end);


    // Query số sản phẩm đã bán
    @Query("""
    SELECT COALESCE(SUM(dhct.soLuong), 0)
    FROM DonHangChiTiet dhct
    JOIN dhct.donHang dh
    WHERE (
        (LOWER(dh.loaiDonHang) LIKE '%online%' AND dh.trangThai = 4) OR
        (LOWER(dh.loaiDonHang) LIKE '%bán hàng%' OR LOWER(dh.loaiDonHang) LIKE '%quầy%') AND dh.trangThai = 1
    ) AND dh.ngayMua BETWEEN :start AND :end
""")
    Long sumProductsAllChannels(@Param("start") LocalDate start, @Param("end") LocalDate end);

    // Tổng SP bán ra theo kênh
    @Query("""
 SELECT COALESCE(SUM(ct.soLuong), 0)
 FROM DonHangChiTiet ct
 JOIN ct.donHang dh
 WHERE LOWER(dh.loaiDonHang) = LOWER(:loai)
   AND dh.trangThai = :status
   AND dh.ngayMua BETWEEN :start AND :end
""")
    Long sumProductsByChannel(@Param("loai") String loai, @Param("status") int status,
                              @Param("start") LocalDate start, @Param("end") LocalDate end);

    // Best seller theo kênh
    @Query("""
    SELECT new com.example.backend.dto.BestSellerProductDTO(
        sp.id, 
        sp.tenSanPham, 
        th.tenThuongHieu, 
        SUM(ct.soLuong),
        sp.imanges  
    )
    FROM DonHangChiTiet ct
    JOIN ct.donHang dh
    JOIN ct.sanPhamChiTiet spct
    JOIN spct.sanPham sp
    JOIN sp.thuongHieu th
    WHERE (
        (LOWER(dh.loaiDonHang) LIKE '%online%' AND dh.trangThai = 4) OR
        (LOWER(dh.loaiDonHang) LIKE '%bán hàng%' OR LOWER(dh.loaiDonHang) LIKE '%quầy%') AND dh.trangThai = 1
    ) AND dh.ngayMua BETWEEN :start AND :end
    GROUP BY sp.id, sp.tenSanPham, th.tenThuongHieu, sp.imanges  
    ORDER BY SUM(ct.soLuong) DESC
""")
    List<BestSellerProductDTO> findBestSellersAllChannels(@Param("start") LocalDate start,
                                                          @Param("end") LocalDate end);


}









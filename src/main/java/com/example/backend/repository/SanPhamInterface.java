package com.example.backend.repository;

import com.example.backend.dto.SanPhanDTO;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import com.example.backend.entity.SanPham;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;
import java.util.Optional;

public interface SanPhamInterface extends JpaRepository<SanPham,Integer> {

//    @Query("SELECT new com.example.backend.dto.SanPhanDTO(" +
//            "sp.id, sp.tenSanPham, MIN(spct.giaBan), sp.danhMuc, sp.thuongHieu, sp.chatLieu, sp.xuatXu, sp.imanges, sp.trangThai) " +
//            "FROM SanPham sp JOIN SanPhamChiTiet spct ON sp.id = spct.sanPham.id " +
//            "WHERE sp.trangThai = 1 " +
//            "GROUP BY sp.id, sp.tenSanPham, sp.danhMuc, sp.thuongHieu, sp.chatLieu, sp.xuatXu, sp.imanges, sp.trangThai")
//    List<SanPhanDTO> findAllActiveProductsWithMinPrice();

    @Query("""
    SELECT new com.example.backend.dto.SanPhanDTO(
        sp.id, sp.tenSanPham, 
        CASE 
            WHEN MIN(spct.giaBanGiamGia) > 0 THEN MIN(spct.giaBanGiamGia) 
            ELSE MIN(spct.giaBan) 
        END, 
        sp.danhMuc, sp.thuongHieu, sp.chatLieu, sp.xuatXu, sp.imanges, sp.trangThai
    ) 
    FROM SanPham sp 
    JOIN SanPhamChiTiet spct ON sp.id = spct.sanPham.id 
    WHERE sp.trangThai = 1 
    GROUP BY sp.id, sp.tenSanPham, sp.danhMuc, sp.thuongHieu, sp.chatLieu, sp.xuatXu, sp.imanges, sp.trangThai
    """)
    List<SanPhanDTO> findAllActiveProductsWithMinPrice();

    List<SanPham> findAllByTrangThai(int trangThai);
    Optional<SanPham> findByTenSanPhamIgnoreCase(String maSanPham);
    List<SanPham> findByTenSanPhamAndDanhMuc_IdAndThuongHieu_IdAndChatLieu_IdAndXuatXu_Id(
            String tenSanPham,
            Integer idDanhMuc,
            Integer idThuongHieu,
            Integer idChatLieu,
            Integer idXuatXu
    );
    List<SanPham> findByDanhMuc_IdAndThuongHieu_IdAndChatLieu_IdAndXuatXu_Id(
            Integer idDanhMuc, Integer idThuongHieu, Integer idChatLieu, Integer idXuatXu
    );



    //    Page<SanPham> findAll(Pageable pageable);
    @Query("""
    SELECT s FROM SanPham s
    WHERE (:idDanhMuc IS NULL OR s.danhMuc.id = :idDanhMuc)
      AND (:idThuongHieu IS NULL OR s.thuongHieu.id = :idThuongHieu)
      AND (:idChatLieu IS NULL OR s.chatLieu.id = :idChatLieu)
      AND (:idXuatXu IS NULL OR s.xuatXu.id = :idXuatXu)
      AND (:trangThai IS NULL OR s.trangThai = :trangThai)
      AND (:search IS NULL OR LOWER(s.tenSanPham) LIKE LOWER(CONCAT('%', :search, '%')))
""")
    Page<SanPham> filterSanPhamPage(
            @Param("idDanhMuc") Integer idDanhMuc,
            @Param("idThuongHieu") Integer idThuongHieu,
            @Param("idChatLieu") Integer idChatLieu,
            @Param("idXuatXu") Integer idXuatXu,
            @Param("trangThai") Integer trangThai,
            @Param("search") String search,
            Pageable pageable
    );

}

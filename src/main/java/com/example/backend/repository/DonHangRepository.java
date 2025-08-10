package com.example.backend.repository;

import com.example.backend.entity.DonHang;
import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;

@Repository
public interface DonHangRepository extends JpaRepository<DonHang, Integer> {
    //    List<DonHang> getAllByTrangThai(Integer id);
    @Query("SELECT d FROM DonHang d WHERE (:trangThai IS NULL OR d.trangThai = :trangThai) AND (:loai IS NULL OR LOWER(d.loaiDonHang) LIKE LOWER(CONCAT('%', :loai, '%')))")
    List<DonHang> findByTrangThaiAndLoaiDonHang(@Param("trangThai") Integer trangThai, @Param("loai") String loaiDonHang);

    List<DonHang> findByKhachHangIdOrderByNgayTaoDesc(Integer idKhachHang);

    // Dùng trong admin lọc đơn theo trạng thái
    List<DonHang> findByTrangThai(Integer trangThai);

    // Tổng doanh thu
    @Query("SELECT SUM(d.tongTien) FROM DonHang d")
    double sumTongTien();

    // Đếm đơn theo trạng thái
    int countByTrangThai(Integer trangThai);

    @EntityGraph(attributePaths = {"donHangChiTiets"})
    @Query("SELECT d FROM DonHang d WHERE d.id = :id")
    DonHang findWithChiTiet(@Param("id") Integer id);

    // Đếm đơn theo trạng thái
    List<DonHang> findAllByGiamGia_Id(Integer idVoucher);


    // Query doanh thu tổng
    @Query("""
    SELECT COALESCE(SUM(dh.tongTien), 0)
    FROM DonHang dh
    WHERE (
        (LOWER(dh.loaiDonHang) LIKE '%online%' AND dh.trangThai = 4) OR
        (LOWER(dh.loaiDonHang) LIKE '%bán hàng%' OR LOWER(dh.loaiDonHang) LIKE '%quầy%') AND dh.trangThai = 1
    ) AND dh.ngayMua BETWEEN :start AND :end
""")
    Double sumRevenueAllChannels(@Param("start") LocalDate start, @Param("end") LocalDate end);

    // Query số đơn hoàn thành
    @Query("""
    SELECT COUNT(dh)
    FROM DonHang dh
    WHERE (
        (LOWER(dh.loaiDonHang) LIKE '%online%' AND dh.trangThai = 4) OR
        (LOWER(dh.loaiDonHang) LIKE '%bán hàng%' OR LOWER(dh.loaiDonHang) LIKE '%quầy%') AND dh.trangThai = 1
    ) AND dh.ngayMua BETWEEN :start AND :end
""")

    Long countCompletedOrdersAllChannels(@Param("start") LocalDate start, @Param("end") LocalDate end);
    // Theo kênh (truyền đúng loai + status)
    @Query("""
 SELECT COALESCE(SUM(dh.tongTien), 0)
 FROM DonHang dh
 WHERE LOWER(dh.loaiDonHang) = LOWER(:loai)
   AND dh.trangThai = :status
   AND dh.ngayMua BETWEEN :start AND :end
""")
    Double sumRevenueByChannel(@Param("loai") String loai, @Param("status") int status,
                               @Param("start") LocalDate start, @Param("end") LocalDate end);

    @Query("""
 SELECT COUNT(dh.id)
 FROM DonHang dh
 WHERE LOWER(dh.loaiDonHang) = LOWER(:loai)
   AND dh.trangThai = :status
   AND dh.ngayMua BETWEEN :start AND :end
""")
    Integer countCompletedOrdersByChannel(@Param("loai") String loai, @Param("status") int status,
                                          @Param("start") LocalDate start, @Param("end") LocalDate end);


    @Query("""
    SELECT COUNT(dh)
    FROM DonHang dh
    WHERE (
        (LOWER(dh.loaiDonHang) LIKE '%online%' AND dh.trangThai = 4) OR
        (LOWER(dh.loaiDonHang) LIKE '%bán hàng%' OR LOWER(dh.loaiDonHang) LIKE '%quầy%') AND dh.trangThai = 1
    ) AND dh.ngayMua = :date
""")
    Long countOrdersByDate(@Param("date") LocalDate date);

}

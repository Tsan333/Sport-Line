package com.example.backend.entity;

import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDate;
import java.util.ArrayList;  // ✅ THÊM: Import ArrayList
import java.util.List;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Entity
@Table(name = "DonHang")
public class DonHang {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    @ManyToOne
    @JoinColumn(name = "idNhanVien")
    private NhanVien nhanVien;

    @ManyToOne
    @JoinColumn(name = "idKhachHang")
    private KhachHang khachHang;

    @ManyToOne
    @JoinColumn(name = "idGiamGia")
    private Voucher giamGia;

    @Column(name="NgayMua")
    private LocalDate ngayMua;

    @Column(name="NgayTao")
    private LocalDate ngayTao;

    @Column(name="LoaiDonHang")
    private String loaiDonHang;

    @Column(name="TrangThai")
    private Integer trangThai;

    @Column(name="TrangThaiTruocKhiHuy")
    private Integer trangThaiTruocKhiHuy;

    @Column(name="TongTien", nullable = false)  // ✅ Thêm nullable = false
    private Double tongTien;

    @Column(name="TongTienGiamGia")
    private Double tongTienGiamGia;

    @Column(name="DiaChiGiaoHang")
    private String diaChiGiaoHang;

    @Column(name="SoDienThoaiGiaoHang")
    private String soDienThoaiGiaoHang;

    @Column(name="EmailGiaoHang")
    private String emailGiaoHang;

    @Column(name="TenNguoiNhan")
    private String tenNguoiNhan;


    @Column(name = "phiVanChuyen", nullable = false)  // ✅ Thêm nullable = false
    private Integer phiVanChuyen;

    @Column(name="GhiChu")
    private String ghiChu;

    public String getGhiChu() { return ghiChu; }
    public void setGhiChu(String ghiChu) { this.ghiChu = ghiChu; }

    // ✅ SỬA: Chỉ giữ 1 dòng và khởi tạo list rỗng
    @OneToMany(mappedBy = "donHang", cascade = CascadeType.ALL, fetch = FetchType.EAGER, orphanRemoval = true)
    private List<DonHangChiTiet> donHangChiTiets = new ArrayList<>();
}
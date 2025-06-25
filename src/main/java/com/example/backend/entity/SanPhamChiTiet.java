package com.example.backend.entity;

import jakarta.persistence.*;
import jakarta.validation.constraints.Min;
import lombok.*;

import java.sql.Date;
import java.time.LocalDateTime;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Entity
@Table(name = "SanPhamChiTiet")

public class SanPhamChiTiet {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "Id")
    private Integer id;

    @Min(value = 1, message = "Số lượng phải lớn hơn 0")
    @Column(name = "SoLuong")
    private Integer soLuong;

    @Column(name = "NgaySanXuat")
    private Date ngaySanXuat;

    @ManyToOne
    @JoinColumn(name = "IdSanPham", referencedColumnName = "Id")
    private SanPham sanPham;

    @ManyToOne
    @JoinColumn(name = "IdKichThuoc", referencedColumnName = "Id")
    private KichThuoc kichThuoc;

    @ManyToOne
    @JoinColumn(name = "IdMauSac", referencedColumnName = "Id")
    private MauSac mauSac;

    @Column(name = "NgayTao")
    private LocalDateTime ngayTao;

    @Column(name = "TrangThai")
    private Integer trangThai;

    @Min(value = 1, message = "Giá bán phải lớn hơn 0")
    @Column(name = "GiaBan")
    private Double giaBan;

    @Min(value = 0, message = "Giá bán giảm giá phải lớn hơn hoặc bằng 0")
    @Column(name = "GiaBanGiamGia")
    private Double giaBanGiamGia;
}

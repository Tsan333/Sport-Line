package com.example.backend.entity;

import jakarta.persistence.*;
import jakarta.validation.constraints.*;
import lombok.*;
import org.springframework.format.annotation.DateTimeFormat;

import java.time.LocalDate;
import java.util.Date;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
@Entity
@Table(name="KhachHang")
public class KhachHang {

    @Id
    @GeneratedValue(strategy= GenerationType.IDENTITY)
    @Column(name="id")
    private Integer id;


    @Column(name = "TenKhachHang")
    private String tenKhachHang;

    @Column(name = "Email")
    private String email;

    @Column(name = "NgaySinh")
    @DateTimeFormat(pattern = "yyyy-MM-dd")
    private Date ngaySinh;

    @Column(name = "GioiTinh")

    private Boolean gioiTinh;

    @Column(name = "DiaChi")

    private String diaChi;

    @Column(name = "SoDienThoai")
    private String soDienThoai;

    @Column(name="matKhau")
    private String matKhau;


    @Column(name = "trangThai")
    private Boolean trangThai = true;

    @Column(name = "MaThongBao")
    private String maThongBao = null; // ✅ Default null


    @Column(name = "ThoiGianThongBao")
    private LocalDate thoiGianThongBao = null; // ✅ Default null








}

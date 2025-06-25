package com.example.backend.entity;

import jakarta.persistence.*;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;
import lombok.*;
import org.springframework.format.annotation.DateTimeFormat;

import java.util.Date;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Entity
@Table(name="KhuyenMai")

public class KhuyenMai {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;


    @NotBlank(message = "Tên khuyến mãi không được để trống")
    @Size(min = 5, max = 30, message = "Tên khuyến mãi phải có từ 5 đến 20 ký tự")
    @Column(name="TenKhuyenMai" ,unique = true)
    private String tenKhuyenMai;


    @Column(name="LoaiKhuyenMai")
    private String loaiKhuyenMai;


    @Column(name="MoTa")
    private String moTa;

    @NotNull
    @Column(name="GiaTri")
    private Float giaTri;

    @NotNull
    @Column(name = "donToiThieu")
    private Float donToiThieu;


    @Temporal(TemporalType.DATE)
    @Column(name="NgayBatDau")
    @DateTimeFormat(pattern = "yyyy-MM-dd")
    @NotNull
    private Date ngayBatDau;

    @Temporal(TemporalType.DATE)
    @Column(name="NgayKetThuc")
    @DateTimeFormat(pattern = "yyyy-MM-dd")
    @NotNull
    private Date ngayKetThuc;

    @NotNull
    @Column(name="TrangThai")
    private Integer trangThai;

}

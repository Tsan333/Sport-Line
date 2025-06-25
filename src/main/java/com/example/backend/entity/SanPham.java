package com.example.backend.entity;

import jakarta.persistence.*;
import jakarta.validation.constraints.NotBlank;
import lombok.*;

import java.util.Date;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Entity
@Table(name = "SanPham")

public class SanPham {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "Id")
    private Integer id;

    @Column(name = "TenSanPham")
    private String tenSanPham;

    @Column(name = "NgayTao")
    private Date ngayTao;

    @ManyToOne
    @JoinColumn(name = "IdThuongHieu", referencedColumnName = "Id")
    private ThuongHieu thuongHieu;

    @ManyToOne
    @JoinColumn(name = "IdXuatXu", referencedColumnName = "Id")
    private XuatXu xuatXu;

    @ManyToOne
    @JoinColumn(name = "IdKhuyenMai", referencedColumnName = "Id")
    private KhuyenMai khuyenMai;

    @Column(name = "Images")
    private String imanges;

    @Column(name = "TrangThai")
    private Integer trangThai;
 }

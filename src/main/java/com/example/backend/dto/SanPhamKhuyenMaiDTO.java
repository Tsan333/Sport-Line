// src/main/java/com/example/backend/dto/SanPhamKhuyenMaiDTO.java
package com.example.backend.dto;

import com.example.backend.entity.ChatLieu;
import com.example.backend.entity.DanhMuc;
import com.example.backend.entity.ThuongHieu;
import com.example.backend.entity.XuatXu;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class SanPhamKhuyenMaiDTO {
    private int id;
    private String tenSanPham;
    private Double giaBan;           // Giá gốc
    private Double giaBanSauGiam;    // Giá sau giảm
    private DanhMuc danhMuc;
    private ThuongHieu thuongHieu;
    private ChatLieu chatLieu;
    private XuatXu xuatXu;
    private String imanges;
    private Integer trangThai;

    // Thông tin khuyến mãi
    private Integer idKhuyenMai;
    private String tenKhuyenMai;
    private Float giaTriKhuyenMai;   // Phần trăm giảm giá

    // Constructor
    public SanPhamKhuyenMaiDTO(Integer id, String tenSanPham, Double giaBan, Double giaBanSauGiam,
                               DanhMuc danhMuc, ThuongHieu thuongHieu, ChatLieu chatLieu,
                               XuatXu xuatXu, String imanges, Integer trangThai,
                               Integer idKhuyenMai, String tenKhuyenMai, Float giaTriKhuyenMai) {
        this.id = id;
        this.tenSanPham = tenSanPham;
        this.giaBan = giaBan;
        this.giaBanSauGiam = giaBanSauGiam;
        this.danhMuc = danhMuc;
        this.thuongHieu = thuongHieu;
        this.chatLieu = chatLieu;
        this.xuatXu = xuatXu;
        this.imanges = imanges;
        this.trangThai = trangThai;
        this.idKhuyenMai = idKhuyenMai;
        this.tenKhuyenMai = tenKhuyenMai;
        this.giaTriKhuyenMai = giaTriKhuyenMai;
    }
}
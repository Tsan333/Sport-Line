package com.example.backend.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class GioHangChiTietResponse {
    private Integer id;
    private Integer soLuong;
    private Double gia;
    private Integer idSanPhamChiTiet;
    private Double giaBan;
    private Double giaBanGiamGia;  // ← Trường mới để lưu giá khuyến mãi
    private String tenSanPham;
    private String imanges;
    private String tenKichThuoc;
    private String tenMauSac;
    private Integer idSanPham;
}
package com.example.backend.DTO;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@AllArgsConstructor
@NoArgsConstructor
@Setter
@Getter
public class DonHangChiTietDTO {
    private Integer id;
    private Integer idDonHang;
    private Integer idSanPhamChiTiet;
    private int soLuong;
    private double gia;
    private double thanhTien;
}

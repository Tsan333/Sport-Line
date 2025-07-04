
package com.example.backend.dto;


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
    private Integer soLuong;
    private Double gia;
    private Double thanhTien;
}

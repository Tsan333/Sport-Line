package com.example.backend.DTO;

import com.example.backend.entity.DonHangChiTiet;
import com.example.backend.entity.KhachHang;
import com.example.backend.entity.NhanVien;
import com.example.backend.entity.Voucher;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDate;
import java.util.List;

@AllArgsConstructor
@NoArgsConstructor
@Setter
@Getter
public class DonHangDTO {

    private Integer id;



    private Integer  idnhanVien;


    private Integer  idkhachHang;


    private Integer  idgiamGia;


    private LocalDate ngayMua;


    private LocalDate ngayTao;


    private String loaiDonHang;


    private String trangThai;


    private double tongTien;


    private Double tongTienGiamGia;


    private List<DonHangChiTiet> donHangChiTiets;
}

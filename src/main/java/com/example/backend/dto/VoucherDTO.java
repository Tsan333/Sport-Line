package com.example.backend.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDateTime;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class VoucherDTO {

    private Integer id;

    private String maVoucher;

    private String tenVoucher;

    private String loaiVoucher;

    private String moTa;

    private Integer soLuong;

    private Double giaTri;

    private Double donToiThieu;

    private LocalDateTime ngayBatDau;

    private LocalDateTime ngayKetThuc;

    private Integer trangThai;
}


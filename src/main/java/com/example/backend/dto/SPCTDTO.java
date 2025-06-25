package com.example.backend.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class SPCTDTO {
    private Integer id;
    private String tenSanPham;
    private Integer soLuong;
    private Double giaBan;
    private String kichThuoc;
    private String mauSac;


}


package com.example.backend.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.Date;

@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
public class KhuyenMaiDTO {




    private Integer id;

    private String tenKhuyenMai;



    private String loaiKhuyenMai;



    private String moTa;


    private float giaTri;


    private Float donToiThieu;


    private Date ngayBatDau;

    private Date ngayKetThuc;

    private int trangThai;
}

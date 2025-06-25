package com.example.backend.dto;

import jakarta.persistence.*;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.springframework.format.annotation.DateTimeFormat;

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

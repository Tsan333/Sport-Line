package com.example.backend.DTO;

import jakarta.persistence.*;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.springframework.format.annotation.DateTimeFormat;

import java.time.LocalDate;
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor

public class NhanVienDTO {


    private Integer id;




    private String tenNhanVien;


    private String email;


    private String soDienThoai;


    private LocalDate ngaySinh;


    private Boolean gioiTinh;


    private String diaChi;



    private Boolean vaiTro;





    private String cccd;


    private Boolean trangThai;
}

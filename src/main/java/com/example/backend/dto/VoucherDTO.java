package com.example.backend.DTO;

import com.example.backend.entity.NhanVien;
import jakarta.persistence.*;
import jakarta.validation.constraints.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.hibernate.annotations.CreationTimestamp;
import org.springframework.format.annotation.DateTimeFormat;

import java.time.LocalDate;
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

    private LocalDate ngayTao;





    private Integer trangThai;


}


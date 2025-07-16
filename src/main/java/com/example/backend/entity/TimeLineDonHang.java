package com.example.backend.entity;

import jakarta.persistence.*;
import lombok.Data;

import java.time.LocalDateTime;

@Entity
@Table(name = "order_timelines")
@Data
public class TimeLineDonHang {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "Id")
    private Integer id;

    @Column(name = "DonHangId")
    private Integer donHangId;

    @Column(name = "OrderCode")
    private String orderCode;

    @Column(name = "TrangThai")
    private String trangThai;

    @Column(name = "NoiDung")
    private String noiDung;

    @Column(name = "ThoiGian")
    private LocalDateTime thoiGian;
}


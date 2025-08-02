package com.example.backend.entity;

import jakarta.persistence.*;
import jakarta.validation.constraints.*;
import lombok.*;
import org.springframework.format.annotation.DateTimeFormat;

import java.time.LocalDate;
import java.sql.Date;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Entity
@Table(name="KhachHang")
public class KhachHang {

    @Id
    @GeneratedValue(strategy= GenerationType.IDENTITY)
    @Column(name="id")
    private Integer id;


    @Column(name = "TenKhachHang")
    @NotEmpty(message = "Tên khách hàng không được để trống!")
    @Size(min = 5, max= 30, message = "Tên khách hàng phải từ 5 đên 30 ký tự!")
    private String tenKhachHang;

    @Column(name = "Email")
    @NotEmpty(message = "Email không được để trống!")
    @Email(message = "Email không đúng định dạng!")
    private String email;

    @Column(name = "NgaySinh")
    @DateTimeFormat(pattern = "yyyy-MM-dd")
    private Date ngaySinh;

    @Column(name = "GioiTinh")
    @NotNull(message = "Bạn chưa chọn giới tính!")
    private Boolean gioiTinh;

    @Column(name = "DiaChi")
    @NotEmpty(message = "Địa chỉ không được để trống")
    @Size(min = 5, max = 100, message = "Địa chỉ phải từ 5 đến 100 ký tự!")
    private String diaChi;

    @Column(name = "SoDienThoai")
    @NotEmpty(message = "Số điện thoại không được để trống!")
    @Size(min = 10, max=10, message = "Số điện thoại phải có đúng 10 chữ sổ!")
    @Pattern(regexp = "^\\d+$", message = "Số điện thoại chỉ được chứa chữ số!")
    private String soDienThoai;

    @Column(name="matKhau")
    private String matKhau;

    @Column(name = "TrangThai")
    @NotNull(message = "Bạn chưa chọn trạng thái!")
    private Boolean trangThai;

    @Column(name = "MaThongBao")
    private String maThongBao;

    @Column(name = "ThoiGianThongBao")
    private LocalDate thoiGianThongBao;



}

package com.example.backend.dto;

import jakarta.persistence.Column;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.Size;
import lombok.Data;

@Data
public class DangKyRequest {


    @NotBlank(message = "Mật khẩu không được trống")
    @Size(min = 6, message = "Mật khẩu phải có ít nhất 6 ký tự")
    private String matKhau;

    @NotBlank(message = "Xác nhận mật khẩu không được để trống")
    private String confirmPassword;

    @Email(message = "Email không hợp lệ")
    private String email;

    @NotBlank(message = "Số điện thoại không được trống")
    private String soDienThoai;

    @Column(name = "TenKhachHang")
    @NotEmpty(message = "Tên khách hàng không được để trống!")
    @Size(min = 5, max= 30, message = "Tên khách hàng phải từ 5 đên 30 ký tự!")
    private String tenKhachHang;

    private Boolean gioiTinh;
    private String diaChi;
}

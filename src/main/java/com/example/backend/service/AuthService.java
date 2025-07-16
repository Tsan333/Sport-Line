package com.example.backend.service;

import com.example.backend.dto.AuthResponse;
import com.example.backend.dto.DangNhapRequest;
import com.example.backend.entity.KhachHang;
import com.example.backend.entity.NhanVien;
import com.example.backend.repository.KhachHangRepository;
import com.example.backend.repository.NhanVienRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Service
public class AuthService {

    @Autowired
    private KhachHangRepository khachHangRepo;
    @Autowired
    private NhanVienRepository nhanVienRepo;

    public AuthResponse dangNhap(DangNhapRequest req) {
        String email = req.getEmail();
        String password = req.getMatKhau();

        // Check Khách hàng trước
        Optional<KhachHang> khOpt = khachHangRepo.findByEmail(email);
        if (khOpt.isPresent()) {
            KhachHang kh = khOpt.get();

            if (!password.equals(kh.getMatKhau())) {
                throw new RuntimeException("Sai mật khẩu khách hàng");
            }

            if (!Boolean.TRUE.equals(kh.getTrangThai())) {
                throw new RuntimeException("Tài khoản khách hàng bị khóa");
            }

            return new AuthResponse(kh.getId(), kh.getTenKhachHang(), "KHACH", "/trang-chu");
        }

        // Nếu không phải khách thì check nhân viên
        Optional<NhanVien> nvOpt = nhanVienRepo.findByEmail(email);
        if (nvOpt.isPresent()) {
            NhanVien nv = nvOpt.get();

            if (!password.equals(nv.getMatKhau())) {
                throw new RuntimeException("Sai mật khẩu nhân viên");
            }

            if (!Boolean.TRUE.equals(nv.getTrangThai())) {
                throw new RuntimeException("Tài khoản nhân viên bị khóa");
            }

            return new AuthResponse(nv.getId(), nv.getTenNhanVien(), "NHANVIEN", "/admin/ban-hang");
        }
        throw new RuntimeException("Email không tồn tại trong hệ thống");
    }
}

package com.example.backend.service;

import com.example.backend.dto.AuthResponse;
import com.example.backend.dto.DangNhapRequest;
import com.example.backend.entity.KhachHang;
import com.example.backend.entity.NhanVien;
import com.example.backend.repository.KhachHangRepository;
import com.example.backend.repository.NhanVienRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Service
public class AuthService {

    @Autowired
    private KhachHangRepository khachHangRepo;
    @Autowired
    private NhanVienRepository nhanVienRepo;

    @Autowired
    private PasswordEncoder passwordEncoder;


    public AuthResponse dangNhap(DangNhapRequest req) {
        String email = req.getEmail();
        String matKhau = req.getMatKhau();

        if (email == null || matKhau == null || email.trim().isEmpty() || matKhau.trim().isEmpty()) {
            throw new RuntimeException("Email hoặc mật khẩu không được để trống!");
        }


        Optional<KhachHang> khOpt = khachHangRepo.findByEmail(email.trim());
        if (khOpt.isPresent()) {
            KhachHang kh = khOpt.get();
            String storedPassword = kh.getMatKhau();


            if (storedPassword == null) {
                throw new RuntimeException("Tài khoản không có mật khẩu!");
            }

            if (storedPassword.startsWith("$2a$")) {
                if (!passwordEncoder.matches(matKhau, storedPassword)) {
                    throw new RuntimeException("Mật khẩu không đúng!");
                }
            } else {
                if (!storedPassword.equals(matKhau)) {
                    throw new RuntimeException("Mật khẩu không đúng (dữ liệu cũ)!");
                }

            }

            if (Boolean.FALSE.equals(kh.getTrangThai())) {
                throw new RuntimeException("Tài khoản đang bị khoá!");
            }

            return new AuthResponse(kh.getId(), kh.getTenKhachHang(), "KHACH", "/trang-chu");
        }


        // Nếu không phải khách thì check nhân viên
        Optional<NhanVien> nvOpt = nhanVienRepo.findByEmail(email.trim());
        if (nvOpt.isPresent()) {
            NhanVien nv = nvOpt.get();
            String storedPassword = nv.getMatKhau();

            if (storedPassword == null) {
                throw new RuntimeException("Tài khoản nhân viên không có mật khẩu!");
            }

            if (storedPassword.startsWith("$2a$")) {
                if (!passwordEncoder.matches(matKhau, storedPassword)) {
                    throw new RuntimeException("Sai mật khẩu nhân viên!");
                }
            } else {
                if (!storedPassword.equals(matKhau)) {
                    throw new RuntimeException("Sai mật khẩu nhân viên (dữ liệu cũ)!");
                }
            }

            if (!Boolean.TRUE.equals(nv.getTrangThai())) {
                throw new RuntimeException("Tài khoản nhân viên bị khoá!");
            }

            return new AuthResponse(nv.getId(), nv.getTenNhanVien(), "NHANVIEN", "/admin/ban-hang");
        }
        throw new RuntimeException("Email không tồn tại trong hệ thống!");
    }
}

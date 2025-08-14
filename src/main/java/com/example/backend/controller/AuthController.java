package com.example.backend.controller;

import com.example.backend.dto.AuthResponse;
import com.example.backend.dto.DangNhapRequest;
import com.example.backend.service.AuthService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.Map;

@RestController
@RequestMapping("/api/auth")
@CrossOrigin(origins = "http://localhost:3000")
public class AuthController {

    @Autowired
    private AuthService authService;

    // ✅ CHỈ GIỮ LẠI API đăng nhập thường
    @PostMapping("/dang-nhap")
    public ResponseEntity<?> dangNhap(@RequestBody DangNhapRequest req) {
        System.out.println("=== DEBUG: Received login request ===");
        System.out.println("Email: " + req.getEmail());
        System.out.println("Password: " + req.getMatKhau());

        try {
            AuthResponse res = authService.dangNhap(req);
            return ResponseEntity.ok(res);
        } catch (RuntimeException e) {
            Map<String, String> errorResponse = new HashMap<>();
            errorResponse.put("message", e.getMessage());
            return ResponseEntity.badRequest().body(errorResponse);
        } catch (Exception e) {
            System.out.println("=== DEBUG: Error occurred ===");
            e.printStackTrace();
            Map<String, String> errorResponse = new HashMap<>();
            errorResponse.put("message", "Có lỗi xảy ra, vui lòng thử lại!");
            return ResponseEntity.internalServerError().body(errorResponse);
        }
    }

}
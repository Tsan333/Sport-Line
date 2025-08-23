package com.example.backend.service;

import com.example.backend.entity.KichThuoc;
import com.example.backend.entity.ThuongHieu;
import com.example.backend.repository.KichThuocInterface;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Locale;
import java.util.Optional;

@Service
public class KichThuocService {

    @Autowired
    private KichThuocInterface kti;

    public List<KichThuoc> getAll() {
        return kti.findAllByTrangThai(1);
    }
    public List<KichThuoc> getAlla2() {
        return kti.findAll();
    }

    public KichThuoc getById(Integer id) {
        return kti.findById(id).orElse(null);
    }
    public List<KichThuoc> searchByName(String name) {
        return kti.findByTenKichThuocContainingIgnoreCase(name);
    }


    public ResponseEntity<?> create(KichThuoc kichThuoc) {
        // Chuẩn hóa tên: trim + loại bỏ khoảng trắng thừa ở giữa
        String tenKichThuoc = normalizeTenKichThuoc(kichThuoc.getTenKichThuoc());

        if (tenKichThuoc.isEmpty()) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("Tên kích thước không được để trống!");
        }

        Optional<KichThuoc> existing = kti.findByTenKichThuocIgnoreCase(tenKichThuoc);
        if (existing.isPresent()) {
            return ResponseEntity.status(HttpStatus.CONFLICT).body("Kích thước đã tồn tại!");
        }

        kichThuoc.setTenKichThuoc(tenKichThuoc);
        KichThuoc newKichThuoc = kti.save(kichThuoc);
        return ResponseEntity.status(HttpStatus.CREATED).body(newKichThuoc);
    }



    private String normalizeTenKichThuoc(String tenKichThuoc) {
        if (tenKichThuoc == null) return "";

        return tenKichThuoc
                .trim() // Loại bỏ khoảng trắng đầu cuối
                .replaceAll("\\s+", "") // Loại bỏ TẤT CẢ khoảng trắng
                .toLowerCase(Locale.ROOT); // Chuyển về chữ thường với locale chuẩn
    }

    public ResponseEntity<?> update(Integer id, KichThuoc kichThuoc) {
        Optional<KichThuoc> current = kti.findById(id);
        if (current.isEmpty()) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body("Không tìm thấy Kích thước với ID: " + id);
        }

        // Chuẩn hóa tên: trim + loại bỏ khoảng trắng thừa ở giữa
        String tenKichThuoc = normalizeTenKichThuoc(kichThuoc.getTenKichThuoc());

        if (tenKichThuoc.isEmpty()) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("Tên kích thước không được để trống!");
        }

        // Kiểm tra trùng lặp với tên đã chuẩn hóa
        Optional<KichThuoc> existing = kti.findByTenKichThuocIgnoreCase(tenKichThuoc);
        if (existing.isPresent() && !existing.get().getId().equals(id)) {
            return ResponseEntity.status(HttpStatus.CONFLICT).body("Tên kích thước đã tồn tại!");
        }

        // Cập nhật tên đã chuẩn hóa vào entity
        kichThuoc.setTenKichThuoc(tenKichThuoc);
        kichThuoc.setId(id);
        KichThuoc updated = kti.save(kichThuoc);
        return ResponseEntity.ok(updated);
    }

    public ResponseEntity<?> delete(Integer id) {
        Optional<KichThuoc> optional = kti.findById(id);
        if (optional.isEmpty()) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body("Kích thước với ID " + id + " không tìm thấy");
        }

        KichThuoc kichThuoc = optional.get();
        kichThuoc.setTrangThai(0);
        return ResponseEntity.ok(kti.save(kichThuoc));
    }
    public void khoiPhucKichThuoc(Integer id) {
        KichThuoc kt = kti.findById(id)
                .orElseThrow(() -> new RuntimeException("Không tìm thấy thương hiệu!"));
        kt.setTrangThai(1); // 1 = Đang hoạt động
        kti.save(kt);
    }

    public List<KichThuoc> getThungRac() {
        return kti.findAllByTrangThai(0);
    }
}



package com.example.backend.service;


import com.example.backend.entity.ThuongHieu;
import com.example.backend.repository.ThuongHieuInterface;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Locale;
import java.util.Optional;

@Service
public class ThuongHieuService {

    @Autowired
    private ThuongHieuInterface thi;

    public List<ThuongHieu> getAll() {
        return thi.findAllByTrangThai(1);
    }
    public List<ThuongHieu> getAllFull() {
        return thi.findAll();
    }

    public ThuongHieu getById(Integer id) {
        return thi.findById(id).orElse(null);
    }
    public List<ThuongHieu> searchByName(String name) {
        return thi.findByTenThuongHieuContainingIgnoreCase(name);
    }

    private String normalizeTenThuongHieu(String tenThuongHieu) {
        if (tenThuongHieu == null) return "";

        return tenThuongHieu
                .trim() // Loại bỏ khoảng trắng đầu cuối
                .replaceAll("\\s+", "") // Loại bỏ TẤT CẢ khoảng trắng
                .toLowerCase(Locale.ROOT); // Chuyển về chữ thường với locale chuẩn
    }

    public ResponseEntity<?> create(ThuongHieu thuongHieu) {
        // ✅ THÊM: Chuẩn hóa tên trước khi kiểm tra
        String tenThuongHieu = normalizeTenThuongHieu(thuongHieu.getTenThuongHieu());

        if (tenThuongHieu.isEmpty()) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("Tên thương hiệu không được để trống!");
        }

        // ✅ SỬA: Sử dụng tên đã chuẩn hóa để kiểm tra trùng lặp
        Optional<ThuongHieu> existing = thi.findByTenThuongHieuIgnoreCase(tenThuongHieu);
        if (existing.isPresent()) {
            return ResponseEntity.status(HttpStatus.CONFLICT).body("Thương hiệu đã tồn tại!");
        }

        // ✅ THÊM: Cập nhật tên đã chuẩn hóa vào entity
        thuongHieu.setTenThuongHieu(tenThuongHieu);
        ThuongHieu saved = thi.save(thuongHieu);
        return ResponseEntity.status(HttpStatus.CREATED).body(saved);
    }

    public ResponseEntity<?> update(Integer id, ThuongHieu thuongHieu) {
        Optional<ThuongHieu> current = thi.findById(id);
        if (current.isEmpty()) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body("Không tìm thấy Thương hiệu với ID: " + id);
        }

        // ✅ THÊM: Chuẩn hóa tên trước khi kiểm tra
        String tenThuongHieu = normalizeTenThuongHieu(thuongHieu.getTenThuongHieu());

        if (tenThuongHieu.isEmpty()) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("Tên thương hiệu không được để trống!");
        }

        // ✅ SỬA: Sử dụng tên đã chuẩn hóa để kiểm tra trùng lặp
        Optional<ThuongHieu> existing = thi.findByTenThuongHieuIgnoreCase(tenThuongHieu);
        if (existing.isPresent() && !existing.get().getId().equals(id)) {
            return ResponseEntity.status(HttpStatus.CONFLICT).body("Tên thương hiệu đã tồn tại!");
        }

        // ✅ THÊM: Cập nhật tên đã chuẩn hóa vào entity
        thuongHieu.setTenThuongHieu(tenThuongHieu);
        thuongHieu.setId(id);
        ThuongHieu updated = thi.save(thuongHieu);
        return ResponseEntity.ok(updated);
    }

    public ResponseEntity<?> delete(Integer id) {
        Optional<ThuongHieu> optionalThuongHieu = thi.findById(id);
        if (optionalThuongHieu.isEmpty()) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body("Thương hiệu với ID " + id + " không tìm thấy");
        }
        ThuongHieu thuongHieu = optionalThuongHieu.get();
        thuongHieu.setTrangThai(0);
        return ResponseEntity.ok(thi.save(thuongHieu));
    }
    public void khoiPhucThuongHieu(Integer id) {
        ThuongHieu th = thi.findById(id)
                .orElseThrow(() -> new RuntimeException("Không tìm thấy thương hiệu!"));
        th.setTrangThai(1); // 1 = Đang hoạt động
        thi.save(th);
    }

    public List<ThuongHieu> getThungRac() {
        return thi.findAllByTrangThai(0);
    }
}

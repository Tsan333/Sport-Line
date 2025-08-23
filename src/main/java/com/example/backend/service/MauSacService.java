
package com.example.backend.service;

import com.example.backend.entity.MauSac;
import com.example.backend.entity.ThuongHieu;
import com.example.backend.repository.MauSacInterface;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Locale;
import java.util.Optional;

@Service
public class MauSacService {

    @Autowired
    private MauSacInterface msi;

    public List<MauSac> getAll() {
        return msi.findAllByTrangThai(1);
    }
    public List<MauSac> getAllFull() {
        return msi.findAll();
    }

    public ResponseEntity<?> getById(Integer id) {
        Optional<MauSac> found = msi.findById(id);
        return found.isPresent()
                ? ResponseEntity.ok(found.get())
                : ResponseEntity.status(HttpStatus.NOT_FOUND).body("Không tìm thấy Màu sắc với ID: " + id);
    }
    public List<MauSac> searchByName(String name) {
        return msi.findByTenMauSacContainingIgnoreCase(name);
    }

    private String normalizeTenMauSac(String tenMauSac) {
        if (tenMauSac == null) return "";

        return tenMauSac
                .trim() // Loại bỏ khoảng trắng đầu cuối
                .replaceAll("\\s+", "") // Loại bỏ TẤT CẢ khoảng trắng
                .toLowerCase(Locale.ROOT); // Chuyển về chữ thường với locale chuẩn
    }

    public ResponseEntity<?> create(MauSac mauSac) {
        // ✅ THÊM: Chuẩn hóa tên trước khi kiểm tra
        String tenMauSac = normalizeTenMauSac(mauSac.getTenMauSac());

        if (tenMauSac.isEmpty()) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("Tên màu sắc không được để trống!");
        }

        // ✅ SỬA: Sử dụng tên đã chuẩn hóa để kiểm tra trùng lặp
        Optional<MauSac> existing = msi.findByTenMauSacIgnoreCase(tenMauSac);
        if (existing.isPresent()) {
            return ResponseEntity.status(HttpStatus.CONFLICT).body("Màu sắc đã tồn tại!");
        }

        // ✅ THÊM: Cập nhật tên đã chuẩn hóa vào entity
        mauSac.setTenMauSac(tenMauSac);
        MauSac saved = msi.save(mauSac);
        return ResponseEntity.status(HttpStatus.CREATED).body(saved);
    }

    public ResponseEntity<?> update(Integer id, MauSac mauSac) {
        Optional<MauSac> current = msi.findById(id);
        if (current.isEmpty()) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body("Không tìm thấy Màu sắc với ID: " + id);
        }

        // ✅ THÊM: Chuẩn hóa tên trước khi kiểm tra
        String tenMauSac = normalizeTenMauSac(mauSac.getTenMauSac());

        if (tenMauSac.isEmpty()) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("Tên màu sắc không được để trống!");
        }

        // ✅ SỬA: Sử dụng tên đã chuẩn hóa để kiểm tra trùng lặp
        Optional<MauSac> existing = msi.findByTenMauSacIgnoreCase(tenMauSac);
        if (existing.isPresent() && !existing.get().getId().equals(id)) {
            return ResponseEntity.status(HttpStatus.CONFLICT).body("Tên màu sắc đã tồn tại!");
        }

        // ✅ THÊM: Cập nhật tên đã chuẩn hóa vào entity
        mauSac.setTenMauSac(tenMauSac);
        mauSac.setId(id);
        MauSac updated = msi.save(mauSac);
        return ResponseEntity.ok(updated);
    }

    public ResponseEntity<?> delete(Integer id) {
        Optional<MauSac> optional = msi.findById(id);
        if (optional.isEmpty()) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body("Màu sắc với ID " + id + " không tìm thấy");
        }

        MauSac mauSac = optional.get();
        mauSac.setTrangThai(0);
        MauSac saved = msi.save(mauSac);
        return ResponseEntity.ok(saved);
    }

    public List<MauSac> getThungRac() {
        return msi.findAllByTrangThai(0);
    }
    public void khoiPhucMauSac(Integer id) {
        MauSac ms = msi.findById(id)
                .orElseThrow(() -> new RuntimeException("Không tìm thấy thương hiệu!"));
        ms.setTrangThai(1); // 1 = Đang hoạt động
        msi.save(ms);
    }
}



package com.example.backend.service;



import com.example.backend.entity.DanhMuc;
import com.example.backend.entity.KichThuoc;
import com.example.backend.repository.DanhMucInterface;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Locale;
import java.util.Optional;

@Service
public class DanhMucService {

    @Autowired
    private DanhMucInterface dmi;

    public List<DanhMuc> getAll() {
        return dmi.findAllByTrangThai(1);
    }
    public List<DanhMuc> getAllFull() {
        return dmi.findAll();
    }
    public DanhMuc getById(Integer id) {
        return dmi.findById(id).orElse(null);
    }

    public List<DanhMuc> searchByName(String name) {
        return dmi.findByTenDanhMucContainingIgnoreCase(name);
    }

    private String normalizeTenDanhMuc(String tenDanhMuc) {
        if (tenDanhMuc == null) return "";

        return tenDanhMuc
                .trim() // Loại bỏ khoảng trắng đầu cuối
                .replaceAll("\\s+", "") // Loại bỏ TẤT CẢ khoảng trắng
                .toLowerCase(Locale.ROOT); // Chuyển về chữ thường với locale chuẩn
    }

    public ResponseEntity<?> create(DanhMuc danhMuc) {
        // ✅ THÊM: Chuẩn hóa tên trước khi kiểm tra
        String tenDanhMuc = normalizeTenDanhMuc(danhMuc.getTenDanhMuc());

        if (tenDanhMuc.isEmpty()) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("Tên danh mục không được để trống!");
        }

        // ✅ SỬA: Lấy tất cả danh mục và so sánh sau khi normalize
        List<DanhMuc> allDanhMuc = dmi.findAll();
        boolean isDuplicate = allDanhMuc.stream()
                .anyMatch(existing -> normalizeTenDanhMuc(existing.getTenDanhMuc()).equals(tenDanhMuc));

        if (isDuplicate) {
            return ResponseEntity.status(HttpStatus.CONFLICT).body("Danh mục đã tồnại!");
        }

        // ✅ THÊM: Cập nhật tên đã chuẩn hóa vào entity
        danhMuc.setTenDanhMuc(tenDanhMuc);
        DanhMuc newDanhMuc = dmi.save(danhMuc);
        return ResponseEntity.status(HttpStatus.CREATED).body(newDanhMuc);
    }

    public ResponseEntity<?> update(Integer id, DanhMuc danhMuc) {
        Optional<DanhMuc> current = dmi.findById(id);
        if (current.isEmpty()) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body("Không tìm thấy Danh mục với ID: " + id);
        }

        // ✅ THÊM: Chuẩn hóa tên trước khi kiểm tra
        String tenDanhMuc = normalizeTenDanhMuc(danhMuc.getTenDanhMuc());

        if (tenDanhMuc.isEmpty()) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("Tên danh mục không được để trống!");
        }

        // ✅ SỬA: Lấy tất cả danh mục và so sánh sau khi normalize
        List<DanhMuc> allDanhMuc = dmi.findAll();
        boolean isDuplicate = allDanhMuc.stream()
                .anyMatch(existing -> !existing.getId().equals(id) &&
                        normalizeTenDanhMuc(existing.getTenDanhMuc()).equals(tenDanhMuc));

        if (isDuplicate) {
            return ResponseEntity.status(HttpStatus.CONFLICT).body("Tên danh mục đã tồnại!");
        }

        // ✅ THÊM: Cập nhật tên đã chuẩn hóa vào entity
        danhMuc.setTenDanhMuc(tenDanhMuc);
        danhMuc.setId(id); // Cập nhật lại ID
        DanhMuc updated = dmi.save(danhMuc);
        return ResponseEntity.ok(updated);
    }

    public ResponseEntity<?> delete(Integer id) {
        Optional<DanhMuc> optionalDanhMuc = dmi.findById(id);
        if (optionalDanhMuc.isEmpty()) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body("Danh mục với ID " + id + " không tìm thấy");
        }

        DanhMuc danhMuc = optionalDanhMuc.get();
        danhMuc.setTrangThai(0);
        DanhMuc saved = dmi.save(danhMuc);
        return ResponseEntity.ok(saved);
    }
    public void khoiPhucDanhMuc(Integer id) {
        DanhMuc dm = dmi.findById(id)
                .orElseThrow(() -> new RuntimeException("Không tìm thấy thương hiệu!"));
        dm.setTrangThai(1); // 1 = Đang hoạt động
        dmi.save(dm);
    }

    public List<DanhMuc> getThungRac() {
        return dmi.findAllByTrangThai(0);
    }
}


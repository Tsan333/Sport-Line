
package com.example.backend.service;



import com.example.backend.entity.KichThuoc;
import com.example.backend.entity.XuatXu;
import com.example.backend.repository.XuatXuInterface;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Locale;
import java.util.NoSuchElementException;
import java.util.Optional;

@Service
public class XuatXuService {

    @Autowired
    private XuatXuInterface xuatXuRepo;

    public List<XuatXu> getAllActive() {
        return xuatXuRepo.findAllByTrangThai(1);
    }
    public List<XuatXu> getAllFull() {
        return xuatXuRepo.findAll();
    }

    public XuatXu getById(Integer id) {
        return xuatXuRepo.findById(id)
                .orElseThrow(() -> new NoSuchElementException("Không tìm thấy Xuất xứ với ID: " + id));
    }
    public List<XuatXu> searchByName(String name) {
        return xuatXuRepo.findByTenXuatXuContainingIgnoreCase(name);
    }

    private String normalizeTenXuatXu(String tenXuatXu) {
        if (tenXuatXu == null) return "";

        return tenXuatXu
                .trim() // Loại bỏ khoảng trắng đầu cuối
                .replaceAll("\\s+", "") // Loại bỏ TẤT CẢ khoảng trắng
                .toLowerCase(Locale.ROOT); // Chuyển về chữ thường với locale chuẩn
    }

    public ResponseEntity<?> create(XuatXu xuatXu) {
        // ✅ THÊM: Chuẩn hóa tên trước khi kiểm tra
        String tenXuatXu = normalizeTenXuatXu(xuatXu.getTenXuatXu());

        if (tenXuatXu.isEmpty()) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("Tên xuất xứ không được để trống!");
        }

        // ✅ SỬA: Sử dụng tên đã chuẩn hóa để kiểm tra trùng lặp
        Optional<XuatXu> existing = xuatXuRepo.findByTenXuatXuIgnoreCase(tenXuatXu);
        if (existing.isPresent()) {
            return ResponseEntity
                    .status(HttpStatus.CONFLICT)
                    .body("Xuất xứ đã tồn tại!");
        }

        // ✅ THÊM: Cập nhật tên đã chuẩn hóa vào entity
        xuatXu.setTenXuatXu(tenXuatXu);
        XuatXu newXuatXu = xuatXuRepo.save(xuatXu);
        return ResponseEntity
                .status(HttpStatus.CREATED)
                .body(newXuatXu);
    }

    public ResponseEntity<?> update(Integer id, XuatXu xuatXu) {
        Optional<XuatXu> current = xuatXuRepo.findById(id);
        if (current.isEmpty()) {
            return ResponseEntity
                    .status(HttpStatus.NOT_FOUND)
                    .body("Không tìm thấy Xuất xứ với ID: " + id);
        }

        // ✅ THÊM: Chuẩn hóa tên trước khi kiểm tra
        String tenXuatXu = normalizeTenXuatXu(xuatXu.getTenXuatXu());

        if (tenXuatXu.isEmpty()) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("Tên xuất xứ không được để trống!");
        }

        // ✅ SỬA: Sử dụng tên đã chuẩn hóa để kiểm tra trùng lặp
        Optional<XuatXu> existing = xuatXuRepo.findByTenXuatXuIgnoreCase(tenXuatXu);
        if (existing.isPresent() && !existing.get().getId().equals(id)) {
            return ResponseEntity
                    .status(HttpStatus.CONFLICT)
                    .body("Tên xuất xứ đã tồn tại!");
        }

        // ✅ SỬA: Cập nhật thông tin với tên đã chuẩn hóa
        XuatXu xuatXuToUpdate = current.get();
        xuatXuToUpdate.setTenXuatXu(tenXuatXu); // Sử dụng tên đã chuẩn hóa
        xuatXuToUpdate.setTrangThai(xuatXu.getTrangThai());

        XuatXu updated = xuatXuRepo.save(xuatXuToUpdate);
        return ResponseEntity.ok(updated);
    }


    public ResponseEntity<?> delete(Integer id) {
        Optional<XuatXu> optional = xuatXuRepo.findById(id);
        if (optional.isEmpty()) {
            return ResponseEntity
                    .status(HttpStatus.NOT_FOUND)
                    .body("Xuất xứ với ID " + id + " không tìm thấy");
        }

        XuatXu xuatXu = optional.get();
        xuatXu.setTrangThai(0);
        XuatXu saved = xuatXuRepo.save(xuatXu);
        return ResponseEntity.ok(saved);
    }
    public void khoiPhucXuatXu(Integer id) {
        XuatXu xx = xuatXuRepo.findById(id)
                .orElseThrow(() -> new RuntimeException("Không tìm thấy thương hiệu!"));
        xx.setTrangThai(1); // 1 = Đang hoạt động
        xuatXuRepo.save(xx);
    }

    public List<XuatXu> getThungRac() {
        return xuatXuRepo.findAllByTrangThai(0);
    }
}


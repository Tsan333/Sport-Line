package com.example.backend.Service;

import com.example.backend.entity.KichThuoc;
import com.example.backend.repository.KichThuocInterface;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
public class KichThuocService {

    @Autowired
    private KichThuocInterface kti;

    public List<KichThuoc> getAll() {
        return kti.findAllByTrangThai(1);
    }

    public KichThuoc getById(Integer id) {
        return kti.findById(id).orElse(null);
    }

    public ResponseEntity<?> create(KichThuoc kichThuoc) {
        Optional<KichThuoc> existing = kti.findByTenKichThuocIgnoreCase(kichThuoc.getTenKichThuoc());
        if (existing.isPresent()) {
            return ResponseEntity.status(HttpStatus.CONFLICT).body("Kích thước đã tồn tại!");
        }
        KichThuoc newKichThuoc = kti.save(kichThuoc);
        return ResponseEntity.status(HttpStatus.CREATED).body(newKichThuoc);
    }

    public ResponseEntity<?> update(Integer id, KichThuoc kichThuoc) {
        Optional<KichThuoc> current = kti.findById(id);
        if (current.isEmpty()) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body("Không tìm thấy Kích thước với ID: " + id);
        }

        Optional<KichThuoc> existing = kti.findByTenKichThuocIgnoreCase(kichThuoc.getTenKichThuoc());
        if (existing.isPresent() && !existing.get().getId().equals(id)) {
            return ResponseEntity.status(HttpStatus.CONFLICT).body("Tên kích thước đã tồn tại!");
        }

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

    public List<KichThuoc> getThungRac() {
        return kti.findAllByTrangThai(0);
    }
}


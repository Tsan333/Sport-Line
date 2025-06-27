
package com.example.backend.service;


import com.example.backend.entity.SanPham;
import com.example.backend.repository.SanPhamInterface;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;


@Service
public class SanPhamService {

    @Autowired
    private SanPhamInterface sanPhamRepo;

    public List<SanPham> getAllActive() {
        return sanPhamRepo.findAllByTrangThai(1);
    }

    public SanPham getById(Integer id) {
        return sanPhamRepo.findById(id)
                .orElseThrow(() -> new RuntimeException("Không tìm thấy sản phẩm với ID: " + id));
    }

    public SanPham create(SanPham sanPham) {
        Optional<SanPham> existing = sanPhamRepo.findByTenSanPhamIgnoreCase(sanPham.getTenSanPham());
        if (existing.isPresent()) {
            throw new RuntimeException("Mã sản phẩm đã tồn tại!");
        }
        sanPham.setTrangThai(1);
        return sanPhamRepo.save(sanPham);
    }

    public SanPham update(Integer id, SanPham sanPham) {
        SanPham current = sanPhamRepo.findById(id)
                .orElseThrow(() -> new RuntimeException("Không tìm thấy sản phẩm với ID: " + id));

        Optional<SanPham> existing = sanPhamRepo.findByTenSanPhamIgnoreCase(sanPham.getTenSanPham());
        if (existing.isPresent() && !existing.get().getId().equals(id)) {
            throw new RuntimeException("Mã sản phẩm đã tồn tại!");
        }

        sanPham.setId(id); // Gán id vào entity
        return sanPhamRepo.save(sanPham);
    }

    public void delete(Integer id) {
        SanPham sanPham = sanPhamRepo.findById(id)
                .orElseThrow(() -> new RuntimeException("Không tìm thấy sản phẩm với ID: " + id));
        sanPham.setTrangThai(0);
        sanPhamRepo.save(sanPham);
    }

    public List<SanPham> getDeleted() {
        return sanPhamRepo.findAllByTrangThai(0);
    }
}


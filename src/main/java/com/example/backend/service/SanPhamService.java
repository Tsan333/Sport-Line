
package com.example.backend.service;


import com.example.backend.dto.SanPhamKhuyenMaiDTO;
import com.example.backend.dto.SanPhanDTO;
import com.example.backend.entity.SanPham;
import com.example.backend.repository.SanPhamInterface;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;

import java.util.List;
import java.util.Locale;
import java.util.Optional;


@Service
public class SanPhamService {

    @Autowired
    private SanPhamInterface sanPhamRepo;

    public List<SanPham> getAllActive() {
        return sanPhamRepo.findAll();
    }

    public List<SanPhamKhuyenMaiDTO> getAllProductsWithPromotion() {
        return sanPhamRepo.findAllProductsWithPromotion();
    }

    public List<SanPhanDTO> getAllActiveProducts() {
        return sanPhamRepo.findAllActiveProductsWithMinPrice();
    }



    public SanPham getById(Integer id) {
        return sanPhamRepo.findById(id)
                .orElseThrow(() -> new RuntimeException("Không tìm thấy sản phẩm với ID: " + id));
    }


    public Page<SanPham> filterSanPhamPage(
            Integer idDanhMuc, Integer idThuongHieu, Integer idChatLieu, Integer idXuatXu,
            Integer trangThai, String search, int page, int size
    ) {
        return sanPhamRepo.filterSanPhamPage(
                idDanhMuc, idThuongHieu, idChatLieu, idXuatXu, trangThai, search, PageRequest.of(page, size)
        );
    }

    private String normalizeTenSanPham(String tenSanPham) {
        if (tenSanPham == null) return "";
        return tenSanPham
                .trim()
                .replaceAll("\\s+", "")
                .toLowerCase(Locale.ROOT);
    }

    public SanPham create(SanPham sanPham) {
        // ✅ Chuẩn hóa tên CHỈ ĐỂ KIỂM TRA trùng lặp
        String tenSanPhamNormalized = normalizeTenSanPham(sanPham.getTenSanPham());

        if (tenSanPhamNormalized.isEmpty()) {
            throw new RuntimeException("Tên sản phẩm không được để trống!");
        }

        // ✅ Kiểm tra trùng lặp với tên đã chuẩn hóa
        List<SanPham> allSanPham = sanPhamRepo.findAll();
        boolean isDuplicate = allSanPham.stream()
                .anyMatch(existing -> normalizeTenSanPham(existing.getTenSanPham()).equals(tenSanPhamNormalized));

        if (isDuplicate) {
            throw new RuntimeException("Tên sản phẩm đã tồnại!");
        }


        // ✅ Giữ nguyên tên gốc từ người dùng
        sanPham.setTrangThai(1);
        return sanPhamRepo.save(sanPham);
    }

    public SanPham update(Integer id, SanPham sanPham) {
        SanPham current = sanPhamRepo.findById(id)
                .orElseThrow(() -> new RuntimeException("Không tìm thấy sản phẩm với ID: " + id));

        // ✅ Chuẩn hóa tên CHỈ ĐỂ KIỂM TRA trùng lặp
        String tenSanPhamNormalized = normalizeTenSanPham(sanPham.getTenSanPham());

        if (tenSanPhamNormalized.isEmpty()) {
            throw new RuntimeException("Tên sản phẩm không được để trống!");
        }

        // ✅ Kiểm tra trùng lặp với tên đã chuẩn hóa
        List<SanPham> allSanPham = sanPhamRepo.findAll();
        boolean isDuplicate = allSanPham.stream()
                .anyMatch(existing -> !existing.getId().equals(id) &&
                        normalizeTenSanPham(existing.getTenSanPham()).equals(tenSanPhamNormalized));

        if (isDuplicate) {
            throw new RuntimeException("Tên sản phẩm đã tồnại!");
        }


        // ✅ Giữ nguyên tên gốc từ người dùng
        sanPham.setId(id);
        return sanPhamRepo.save(sanPham);
    }

    public void delete(Integer id) {
        SanPham sanPham = sanPhamRepo.findById(id)
                .orElseThrow(() -> new RuntimeException("Không tìm thấy sản phẩm với ID: " + id));
        sanPham.setTrangThai(0);
        sanPhamRepo.save(sanPham);
    }
    public void restoreSanPham(Integer id) {
        Optional<SanPham> optional = sanPhamRepo.findById(id);
        if (optional.isPresent()) {
            SanPham sp = optional.get();
            sp.setTrangThai(1); // 1 = đang bán, 0 = đã xóa
            sanPhamRepo.save(sp);
        } else {
            throw new RuntimeException("Không tìm thấy sản phẩm");
        }
    }

    public List<SanPham> getDeleted() {
        return sanPhamRepo.findAllByTrangThai(0);
    }

    public Page<SanPham> getSanPhamPage(int page, int size) {
        return sanPhamRepo.findAll(PageRequest.of(page, size));
    }










}


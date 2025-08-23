package com.example.backend.controller;


import com.example.backend.dto.SanPhamKhuyenMaiDTO;
import com.example.backend.dto.SanPhanDTO;
import org.springframework.data.domain.Page;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;
import com.example.backend.entity.SanPham;

import com.example.backend.service.SanPhamService;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

import java.util.List;


@RestController
@RequestMapping("/api/san-pham")
public class SanPhamRestController {

    @Autowired
    private SanPhamService sanPhamService;

    @GetMapping("/getAll")
    public List<SanPham> getAllActive() {
        return sanPhamService.getAllActive();
    }

    @GetMapping("/getAllOnline")
    public List<SanPhanDTO> getAllOnline() {
        return sanPhamService.getAllActiveProducts();
    }

    @GetMapping("/sp-co-khuyen-mai")
    public List<SanPhamKhuyenMaiDTO> getAllWithPromotion() {
        return sanPhamService.getAllProductsWithPromotion();
    }

    @GetMapping("/{id}")
    public ResponseEntity<?> getById(@PathVariable Integer id) {
        try {
            return ResponseEntity.ok(sanPhamService.getById(id));
        } catch (RuntimeException e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(e.getMessage());
        }
    }
    @GetMapping("/phan-trang")
    public ResponseEntity<Page<SanPham>> filterSanPhamPage(
            @RequestParam(required = false) Integer idDanhMuc,
            @RequestParam(required = false) Integer idThuongHieu,
            @RequestParam(required = false) Integer idChatLieu,
            @RequestParam(required = false) Integer idXuatXu,
            @RequestParam(required = false) Integer trangThai,
            @RequestParam(required = false) String search,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "5") int size
    ) {
        return ResponseEntity.ok(
                sanPhamService.filterSanPhamPage(idDanhMuc, idThuongHieu, idChatLieu, idXuatXu, trangThai, search, page, size)
        );
    }

    @PostMapping("/add")
    public ResponseEntity<?> create(@Valid @RequestBody SanPham sanPham) {
        try {
            return ResponseEntity.status(HttpStatus.CREATED).body(sanPhamService.create(sanPham));
        } catch (RuntimeException e) {
            return ResponseEntity.status(HttpStatus.CONFLICT).body(e.getMessage());
        }
    }

    @PutMapping("/{id}")
    public ResponseEntity<?> update(@PathVariable Integer id, @Valid @RequestBody SanPham sanPham) {
        try {
            return ResponseEntity.ok(sanPhamService.update(id, sanPham));
        } catch (RuntimeException e) {
            return ResponseEntity.status(HttpStatus.CONFLICT).body(e.getMessage());
        }
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<?> delete(@PathVariable Integer id) {
        try {
            sanPhamService.delete(id);
            return ResponseEntity.ok("Đã chuyển sản phẩm vào thùng rác.");
        } catch (RuntimeException e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(e.getMessage());
        }
    }
    @PutMapping("khoi-phuc/{id}")
    public ResponseEntity<?> restoreSanPham(@PathVariable Integer id) {
        try {
            sanPhamService.restoreSanPham(id);
            return ResponseEntity.ok("Khôi phục thành công");
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("Khôi phục thất bại");
        }
    }

    @GetMapping("/thung-rac")
    public List<SanPham> getDeleted() {
        return sanPhamService.getDeleted();
    }


}



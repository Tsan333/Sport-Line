package com.example.backend.controller;


import com.example.backend.Service.SanPhamService;
import com.example.backend.entity.SanPham;

import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
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

    @GetMapping("/{id}")
    public ResponseEntity<?> getById(@PathVariable Integer id) {
        try {
            return ResponseEntity.ok(sanPhamService.getById(id));
        } catch (RuntimeException e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(e.getMessage());
        }
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

    @GetMapping("/thung-rac")
    public List<SanPham> getDeleted() {
        return sanPhamService.getDeleted();
    }
}



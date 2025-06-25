package com.example.backend.controller;

import com.example.backend.Service.SPCTService;
import com.example.backend.entity.SanPhamChiTiet;

import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/san-pham-chi-tiet")
public class SPCTRestController {

    @Autowired
    private SPCTService service;

    @GetMapping("/getAll")
    public ResponseEntity<List<SanPhamChiTiet>> getAll() {
        return ResponseEntity.ok(service.getAll());
    }

    @GetMapping("/{id}")
    public ResponseEntity<SanPhamChiTiet> getById(@PathVariable Integer id) {
        return ResponseEntity.ok(service.getById(id));
    }

    @PostMapping("/add")
    public ResponseEntity<SanPhamChiTiet> create(@RequestBody @Valid SanPhamChiTiet s) {
        return ResponseEntity.status(HttpStatus.CREATED).body(service.create(s));
    }

    @PutMapping("/{id}")
    public ResponseEntity<SanPhamChiTiet> update(@PathVariable Integer id,
                                                 @RequestBody @Valid SanPhamChiTiet s) {
        return ResponseEntity.ok(service.update(id, s));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> delete(@PathVariable Integer id) {
        service.delete(id);
        return ResponseEntity.noContent().build();
    }
}


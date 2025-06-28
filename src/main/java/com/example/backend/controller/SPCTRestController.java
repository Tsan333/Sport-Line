package com.example.backend.controller;


import com.example.backend.dto.SPCTDTO;
import com.example.backend.entity.SanPhamChiTiet;


import com.example.backend.service.SPCTService;
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

    public ResponseEntity<List<SPCTDTO>> getAllForOffline() {
        return ResponseEntity.ok(service.getAllForOffline());
    }

    @GetMapping("/{id}")
    public ResponseEntity<List<SanPhamChiTiet> > getSPCTDTOById(@PathVariable Integer id) {
        return ResponseEntity.ok(service.getSPCTDTOById(id));
    }

    @GetMapping("/search")
    public ResponseEntity<List<SPCTDTO>> getByTen(@RequestParam String keyword) {
        return ResponseEntity.ok(service.searchByTenSanPham(keyword));

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


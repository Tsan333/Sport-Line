package com.example.backend.controller;


import com.example.backend.dto.SPCTDTO;
import com.example.backend.dto.SPCTRequest;
import com.example.backend.dto.SanPhamDonHangResponse;
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
    public ResponseEntity<List<SPCTDTO>> getSPCTDTOByIdSP(@PathVariable Integer id) {
        return ResponseEntity.ok(service.getSPCTDTOByIdSP(id));
    }

    @GetMapping("/search")
    public ResponseEntity<List<SPCTDTO>> getByTen(@RequestParam String keyword) {
        return ResponseEntity.ok(service.searchByTenSanPham(keyword));
    }

    @GetMapping("/san-pham/{id}")
    public ResponseEntity<List<SanPhamDonHangResponse>> getSanPhamByDonHang(@PathVariable("id") Integer id) {
        return ResponseEntity.ok(service.getSanPhamByDonHang(id));
    }

    @PostMapping("/them/{idSanPham}")
    public ResponseEntity<?> themBienThe(
            @PathVariable Integer idSanPham,
            @RequestBody SPCTRequest request) {
        try {
            SanPhamChiTiet spct = service.createSanPhamChiTiet(idSanPham, request);
            return ResponseEntity.ok(spct);
        } catch (RuntimeException e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        }
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


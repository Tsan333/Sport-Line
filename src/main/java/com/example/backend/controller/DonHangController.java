package com.example.backend.controller;



import com.example.backend.dto.DonHangDTO;
import com.example.backend.service.DonHangService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api")
public class DonHangController {


    @Autowired
    private DonHangService donHangService;

    @GetMapping("/donhang")
    public ResponseEntity<List<DonHangDTO>> getAll() {
        return ResponseEntity.ok(donHangService.getAll());
    }

    @GetMapping("/donhang/{id}")
    public ResponseEntity<DonHangDTO> getById(@PathVariable Integer id) {
        DonHangDTO dto = donHangService.getById(id);
        return dto != null ? ResponseEntity.ok(dto) : ResponseEntity.notFound().build();
    }

    @GetMapping("/donhang/hoanthanh")
    public ResponseEntity<List<DonHangDTO>> hoanThanh() {
        return ResponseEntity.ok(donHangService.getByTrangThai(1));
    }

    @GetMapping("/donhang/chuahoanthanh")
    public ResponseEntity<List<DonHangDTO>> chuahoanThanh() {
        return ResponseEntity.ok(donHangService.getByTrangThai(0));
    }

    @PostMapping("/donhang/create")
    public ResponseEntity<DonHangDTO> create(@RequestBody DonHangDTO dto) {
        return ResponseEntity.ok(donHangService.create(dto));
    }

    @PutMapping("/donhang/xacnhanthanhtoan/{id}")
    public ResponseEntity<DonHangDTO> xacnhanthanhtoan(@PathVariable Integer id) {
        DonHangDTO updated = donHangService.xacNhanDonHang(id);
        return updated != null ? ResponseEntity.ok(updated) : ResponseEntity.notFound().build();
    }

    @PutMapping("/donhang/update/{id}")
    public ResponseEntity<DonHangDTO> update(@PathVariable Integer id, @RequestBody DonHangDTO dto) {
        DonHangDTO updated = donHangService.update(id, dto);
        return updated != null ? ResponseEntity.ok(updated) : ResponseEntity.notFound().build();
    }

    @DeleteMapping("/donhang/delete/{id}")
    public ResponseEntity<Void> delete(@PathVariable Integer id) {
        donHangService.delete(id);
        return ResponseEntity.ok().build();
    }
}

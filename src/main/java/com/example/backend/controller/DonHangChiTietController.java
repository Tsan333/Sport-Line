package com.example.backend.controller;



import com.example.backend.dto.DonHangChiTietDTO;
import com.example.backend.service.DonHangChiTietService;


import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;


@RestController
@RequestMapping("/api")
public class DonHangChiTietController {

    @Autowired
    private DonHangChiTietService chiTietService;

    @GetMapping("/donhangchitiet")
    public ResponseEntity<List<DonHangChiTietDTO>> getAll() {
        return ResponseEntity.ok(chiTietService.getAll());
    }

    @GetMapping("/donhangchitiet/{id}")
    public ResponseEntity<DonHangChiTietDTO> getById(@PathVariable int id) {
        DonHangChiTietDTO dto = chiTietService.getById(id);
        return dto != null ? ResponseEntity.ok(dto) : ResponseEntity.notFound().build();
    }

    @PostMapping("/donhangchitiet/create")
    public ResponseEntity<DonHangChiTietDTO> create(@RequestBody DonHangChiTietDTO dto) {
        return ResponseEntity.ok(chiTietService.create(dto));
    }

    @PutMapping("/donhangchitiet/update/{id}")
    public ResponseEntity<DonHangChiTietDTO> update(@PathVariable int id, @RequestBody DonHangChiTietDTO dto) {
        DonHangChiTietDTO updated = chiTietService.update(id, dto);
        return updated != null ? ResponseEntity.ok(updated) : ResponseEntity.notFound().build();
    }

    @DeleteMapping("/donhangchitiet/delete/{id}")
    public ResponseEntity<Void> delete(@PathVariable int id) {
        chiTietService.delete(id);
        return ResponseEntity.ok().build();
    }
}

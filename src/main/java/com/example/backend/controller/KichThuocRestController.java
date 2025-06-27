package com.example.backend.controller;


import com.example.backend.entity.KichThuoc;

import com.example.backend.service.KichThuocService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;


@RestController
@RequestMapping("/api/kich-thuoc")
public class KichThuocRestController {

    @Autowired
    private KichThuocService kichThuocService;

    @GetMapping("/getAll")
    public List<KichThuoc> getAll() {
        return kichThuocService.getAll();
    }

    @GetMapping("/getById/{id}")
    public KichThuoc getById(@PathVariable Integer id) {
        return kichThuocService.getById(id);
    }

    @PostMapping("/add")
    public ResponseEntity<?> create(@RequestBody KichThuoc kichThuoc) {
        return kichThuocService.create(kichThuoc);
    }

    @PutMapping("/update/{id}")
    public ResponseEntity<?> update(@PathVariable Integer id, @RequestBody KichThuoc kichThuoc) {
        return kichThuocService.update(id, kichThuoc);
    }

    @DeleteMapping("/del/{id}")
    public ResponseEntity<?> delete(@PathVariable Integer id) {
        return kichThuocService.delete(id);
    }

    @GetMapping("/getThungRac")
    public List<KichThuoc> getThungRac() {
        return kichThuocService.getThungRac();
    }
}


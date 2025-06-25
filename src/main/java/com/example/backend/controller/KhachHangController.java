package com.example.backend.controller;

import com.example.backend.DTO.KhachHangReponseDTO;
import com.example.backend.Service.KhachHangService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api")
public class KhachHangController {
    @Autowired
    KhachHangService khachHangService;

    @GetMapping("/khachhang")
    public ResponseEntity<List<KhachHangReponseDTO>> getall() {
        return ResponseEntity.ok(khachHangService.findAll());
    }

    @GetMapping("/khachhang/{id}")
    public ResponseEntity<KhachHangReponseDTO> getbyid(@PathVariable int id) {
        KhachHangReponseDTO khachHangDTO = khachHangService.findAllbyid(id);
        if(khachHangDTO==null){
            return ResponseEntity.notFound().build();
        }


        return ResponseEntity.ok(khachHangDTO);
    }

    @PostMapping("/khachhang/create")
    public ResponseEntity<KhachHangReponseDTO>  create(@RequestBody KhachHangReponseDTO khachHangDTO) {
        KhachHangReponseDTO dto = khachHangService.create(khachHangDTO);
        return  ResponseEntity.ok(dto);
    }

    @DeleteMapping("/khachhang/delete/{id}")
    public Boolean delete(@PathVariable int id) {
        return khachHangService.deleteById(id);
    }

    @PutMapping("/khachhang/update/{id}")
    public ResponseEntity<KhachHangReponseDTO> update(@PathVariable int id, @RequestBody KhachHangReponseDTO dto) {
        return ResponseEntity.ok(khachHangService.update(id, dto));
    }
}

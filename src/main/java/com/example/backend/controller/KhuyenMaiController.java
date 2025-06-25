package com.example.backend.controller;




import com.example.backend.dto.KhuyenMaiDTO;
import com.example.backend.service.KhuyenMaiService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api")
public class KhuyenMaiController {

    @Autowired
    private KhuyenMaiService khuyenMaiService;

    @GetMapping("/khuyenmai")
    public ResponseEntity<List<KhuyenMaiDTO>> getall(){
        return ResponseEntity.ok(khuyenMaiService.getall());

    }

    @GetMapping("/khuyenmai/{id}")
    public ResponseEntity<KhuyenMaiDTO> getbyid(@PathVariable Integer id){
        return ResponseEntity.ok(khuyenMaiService.findById(id));
    }

    @PostMapping("/khuyenmai/create")
    public ResponseEntity<KhuyenMaiDTO> create(@RequestBody KhuyenMaiDTO khuyenMaiDTO){
        KhuyenMaiDTO dto = khuyenMaiService.create(khuyenMaiDTO);
        return ResponseEntity.ok(dto);
    }

    @PutMapping("/khuyenmai/update/{id}")
    public ResponseEntity<KhuyenMaiDTO> update(@PathVariable int id, @RequestBody KhuyenMaiDTO dto) {
        return ResponseEntity.ok(khuyenMaiService.update(id,dto));
    }

    @DeleteMapping("/khuyenmai/delete/{id}")
    public Boolean delete(@PathVariable int id){
        return khuyenMaiService.delete(id);
    }
}

package com.example.backend.controller;




import com.example.backend.dto.NhanVienDTO;
import com.example.backend.dto.PageReSponse;
import com.example.backend.service.NhanVienService;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.List;

@RestController
@RequestMapping("/api")
public class NhanVienController {

    @Autowired
    private NhanVienService nhanVienService;

    @GetMapping("/nhanvien")
    public ResponseEntity<List<NhanVienDTO>> getall(){
        return ResponseEntity.ok(nhanVienService.findall());
    }

    @GetMapping("/nhanvien/{id}")
    public ResponseEntity<NhanVienDTO> getbyid(@PathVariable Integer id){
        return ResponseEntity.ok(nhanVienService.findById(id));
    }

    @GetMapping("/nhanvien/page")
    public ResponseEntity<PageReSponse<NhanVienDTO>> getPage(@RequestParam int page,
                                                             @RequestParam int size) {
        PageReSponse<NhanVienDTO> response = nhanVienService.getPaged(page, size);
        return ResponseEntity.ok(response);
    }

    @PostMapping("/nhanvien/create")
    public ResponseEntity<NhanVienDTO> create(@RequestBody NhanVienDTO nhanVienDTO){
        NhanVienDTO dto = nhanVienService.create(nhanVienDTO);
        return ResponseEntity.ok(dto);
    }

    @PutMapping("/nhanvien/update/{id}")
    public ResponseEntity<NhanVienDTO> update(@PathVariable int id, @RequestBody NhanVienDTO dto) {
        return ResponseEntity.ok(nhanVienService.update(id,dto));
    }

    @DeleteMapping("/nhanvien/delete/{id}")
    public Boolean delete(@PathVariable int id){
        return nhanVienService.delete(id);
    }

    @GetMapping("/nhanvien/search")
    public ResponseEntity<List<NhanVienDTO>> search(@RequestParam String keyword) {
        return ResponseEntity.ok(nhanVienService.search(keyword));
    }

    @GetMapping("/nhanvien/export")
    public void exportNhanVien(HttpServletResponse response) throws IOException {
        response.setContentType("application/octet-stream");
        response.setHeader("Content-Disposition", "attachment; filename=nhanvien.xlsx");

        nhanVienService.exportExcel(response.getOutputStream());
    }
}

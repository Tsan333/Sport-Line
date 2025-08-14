package com.example.backend.controller;




import com.example.backend.dto.DangKyRequest;
import com.example.backend.dto.KhachHangReponseDTO;

import com.example.backend.dto.PageReSponse;
import com.example.backend.service.KhachHangService;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

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
    @GetMapping("/khachhang/page")
    public ResponseEntity<PageReSponse<KhachHangReponseDTO>> getPage(@RequestParam int page,
                                                                     @RequestParam int size) {
        PageReSponse<KhachHangReponseDTO> response = khachHangService.getPaged(page, size);
        return ResponseEntity.ok(response);
    }

    // KhachHangController.java
    @PostMapping("/khachhang/create")
    public ResponseEntity<?> create(@RequestBody KhachHangReponseDTO khachHangDTO) {
        try {
            KhachHangReponseDTO dto = khachHangService.create(khachHangDTO);
            return ResponseEntity.ok(dto);
        } catch (RuntimeException e) {
            // Lỗi chủ động (ví dụ: số điện thoại đã tồn tại)
            return ResponseEntity
                    .badRequest()
                    .body(java.util.Collections.singletonMap("message", e.getMessage()));
        } catch (Exception e) {
            // Bắt mọi lỗi khác (ví dụ: Query did not return a unique result)
            return ResponseEntity
                    .badRequest()
                    .body(java.util.Collections.singletonMap("message", "Dữ liệu khách hàng đã tồn tại!"));
        }
    }

    @DeleteMapping("/khachhang/delete/{id}")
    public Boolean delete(@PathVariable int id) {
        return khachHangService.deleteById(id);
    }

    @PutMapping("/khachhang/update/{id}")
    public ResponseEntity<KhachHangReponseDTO> update(@PathVariable int id, @RequestBody KhachHangReponseDTO dto) {
        return ResponseEntity.ok(khachHangService.update(id, dto));
    }
    @PostMapping("/dang-ky")
    public ResponseEntity<Map<String, Object>> dangKy(@Valid @RequestBody DangKyRequest req) {
        try {
            khachHangService.dangKyKhach(req);

            Map<String, Object> response = new HashMap<>();
            response.put("success", true);
            response.put("message", "Đăng ký khách hàng thành công!");

            return ResponseEntity.ok(response);

        } catch (RuntimeException e) {
            Map<String, Object> response = new HashMap<>();
            response.put("success", false);
            response.put("message", e.getMessage());

            return ResponseEntity.badRequest().body(response);

        } catch (Exception e) {
            Map<String, Object> response = new HashMap<>();
            response.put("success", false);
            response.put("message", "Lỗi hệ thống: " + e.getMessage());

            return ResponseEntity.internalServerError().body(response);
        }
    }

    @GetMapping("/khachhang/search")
    public ResponseEntity<List<KhachHangReponseDTO>> search(@RequestParam String keyword) {
        return ResponseEntity.ok(khachHangService.search(keyword));
    }
}

package com.example.backend.controller;





import com.example.backend.dto.KhuyenMaiDTO;

import com.example.backend.entity.KhuyenMai;
import com.example.backend.entity.SanPhamChiTiet;
import com.example.backend.service.KhuyenMaiService;
import com.example.backend.service.SPCTService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api")
public class KhuyenMaiController {

    @Autowired
    private KhuyenMaiService khuyenMaiService;

    @Autowired
    private SPCTService spctService;

    @PostMapping("/khuyen-mai/ap-dung/{idKhuyenMai}")
    public ResponseEntity<List<SanPhamChiTiet>> apDungKhuyenMai(
            @PathVariable Integer idKhuyenMai,
            @RequestBody List<Integer> ids) {
        List<SanPhamChiTiet> ketQua = spctService.addSanPhamDuocKhuyenMai(idKhuyenMai, ids);
        return ResponseEntity.ok(ketQua);
    }

    @PostMapping("/khuyen-mai/tat/{id}")
    public ResponseEntity<KhuyenMai> tat(@PathVariable Integer id){
        return ResponseEntity.ok(khuyenMaiService.tatKhuyenMai(id));
    }
    @GetMapping("/san-pham-chi-tiet/available-for-promotion/{khuyenMaiId}")
    public ResponseEntity<List<SanPhamChiTiet>> getAvailableProductsForPromotion(@PathVariable Integer khuyenMaiId) {
        try {
            List<SanPhamChiTiet> availableProducts = spctService.getAvailableProductsForPromotion(khuyenMaiId);
            return ResponseEntity.ok(availableProducts);
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(null);
        }
    }

    @PostMapping("/khuyen-mai/{khuyenMaiId}/bo-ap-dung")
    public ResponseEntity<String> removePromotionFromProducts(
            @PathVariable Integer khuyenMaiId,
            @RequestBody List<Integer> productIds) {
        try {
            spctService.removePromotionFromProducts(khuyenMaiId, productIds);
            return ResponseEntity.ok("Bỏ áp dụng khuyến mãi thành công");
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("Lỗi khi bỏ áp dụng khuyến mãi");
        }
    }


    @GetMapping("/khuyenmai")
    public ResponseEntity<List<KhuyenMaiDTO>> getall(){
        khuyenMaiService.updateActiveKhuyenMai();
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

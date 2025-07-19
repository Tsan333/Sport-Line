    package com.example.backend.controller;



    import com.example.backend.dto.DonHangDTO;
    import com.example.backend.dto.UpdateKhachHangRequest;
    import com.example.backend.dto.UpdateVoucherDonHangRequest;
    import com.example.backend.dto.XacNhanThanhToanDTO;
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
        @GetMapping("/donhang/getAllHoanThanh")
        public ResponseEntity<List<DonHangDTO>> hoanThanh() {
            // Ví dụ: chỉ lấy trạng thái=1, loaiDonHang="online"
            return ResponseEntity.ok(donHangService.filterByTrangThaiAndLoai(1, "Bán hàng tại quầy"));
        }


        @GetMapping("/donhang/chuahoanthanh")
        public ResponseEntity<List<DonHangDTO>> chuahoanthanh() {
            // Ví dụ: chỉ lấy trạng thái=1, loaiDonHang="online"
            return ResponseEntity.ok(donHangService.filterByTrangThaiAndLoai(0, "Bán hàng tại quầy"));
        }




        @PostMapping("/donhang/create")
        public ResponseEntity<DonHangDTO> create(@RequestBody DonHangDTO dto) {
            return ResponseEntity.ok(donHangService.create(dto));
        }

        @PutMapping("/donhang/update/{id}")
        public ResponseEntity<DonHangDTO> update(@PathVariable Integer id, @RequestBody DonHangDTO dto) {
            DonHangDTO updated = donHangService.update(id, dto);
            return updated != null ? ResponseEntity.ok(updated) : ResponseEntity.notFound().build();
        }

        @PutMapping("/update-voucher/{idDonHang}")
        public ResponseEntity<?> updateVoucher(
                @PathVariable Integer idDonHang,
                @RequestBody UpdateVoucherDonHangRequest request) {
            DonHangDTO updated = donHangService.updateVoucher(idDonHang, request.getIdgiamGia());
            if (updated != null) {
                return ResponseEntity.ok().body(updated);
            } else {
                return ResponseEntity.badRequest().body("Không tìm thấy đơn hàng hoặc voucher");
            }
        }
        @PutMapping("/update-khachhang/{idKhachHang}")
        public ResponseEntity<?> updateKhachHang(
                @PathVariable Integer idKhachHang,
                @RequestBody UpdateKhachHangRequest request) {
            DonHangDTO updated = donHangService.updateKhachHang(idKhachHang, request.getIdkhachHang());
            if (updated != null) {
                return ResponseEntity.ok(updated);
            } else {
                return ResponseEntity.badRequest().body("Không tìm thấy đơn hàng hoặc khách hàng");
            }
        }
        @PutMapping("/xacnhanthanhtoan/{id}")
        public ResponseEntity<DonHangDTO> xacnhanthanhtoan(
                @PathVariable Integer id,
                @RequestBody XacNhanThanhToanDTO request) {
            DonHangDTO updated = donHangService.xacNhanDonHang(
                    id,
                    request.getTongTien(),
                    request.getIdkhachHang(),
                    request.getTenKhachHang(),
                    request.getEmail(),         // Thêm dòng này
                    request.getSoDienThoai()   // Thêm dòng này
            );
            return updated != null ? ResponseEntity.ok(updated) : ResponseEntity.notFound().build();
        }

        @DeleteMapping("/donhang/delete/{id}")
        public ResponseEntity<Void> delete(@PathVariable Integer id) {
            donHangService.delete(id);
            return ResponseEntity.ok().build();
        }
    }

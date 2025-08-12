package com.example.backend.controller;



import com.example.backend.dto.*;
import com.example.backend.entity.DonHang;
import com.example.backend.enums.TrangThaiDonHang;
import com.example.backend.repository.DonHangRepository;
import com.example.backend.service.DonHangService;
import com.example.backend.service.VoucherService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.http.HttpStatus;
import java.util.Collections;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api")
public class DonHangController {



    @Autowired
    private DonHangRepository donHangRepository;

    @Autowired
    private VoucherService voucherService;

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
    @GetMapping("/donhang/don-online")
    public ResponseEntity<List<DonHangDTO>> donOnline() {
        // Ví dụ: chỉ lấy trạng thái=1, loaiDonHang="online"
        return ResponseEntity.ok(donHangService.filterByTrangThaiAndLoai(null, "Bán hàng tại quầy"));
    }


    @GetMapping("/donhang/choxacnhan")
    public ResponseEntity<List<DonHangDTO>> choXacNhan() {
        // Ví dụ: chỉ lấy trạng thái=1, loaiDonHang="online"
        return ResponseEntity.ok(donHangService.filterByTrangThaiAndLoai(0, "online"));
    }
    @GetMapping("/donhang/daxacnhan")
    public ResponseEntity<List<DonHangDTO>> daXacNhan() {
        // Ví dụ: chỉ lấy trạng thái=1, loaiDonHang="online"
        return ResponseEntity.ok(donHangService.filterByTrangThaiAndLoai(1, "online"));
    }
    @GetMapping("/donhang/dangcbi")
    public ResponseEntity<List<DonHangDTO>> dangCB() {
        // Ví dụ: chỉ lấy trạng thái=1, loaiDonHang="online"
        return ResponseEntity.ok(donHangService.filterByTrangThaiAndLoai(2, "online"));
    }
    @GetMapping("/donhang/danggiao")
    public ResponseEntity<List<DonHangDTO>> dangGiao() {
        // Ví dụ: chỉ lấy trạng thái=1, loaiDonHang="online"
        return ResponseEntity.ok(donHangService.filterByTrangThaiAndLoai(3, "online"));
    }
    @GetMapping("/donhang/dagiao")
    public ResponseEntity<List<DonHangDTO>> daGiao() {
        // Ví dụ: chỉ lấy trạng thái=1, loaiDonHang="online"
        return ResponseEntity.ok(donHangService.filterByTrangThaiAndLoai(4, "online"));
    }
    @GetMapping("/donhang/dahuy")
    public ResponseEntity<List<DonHangDTO>> daHuy() {
        // Ví dụ: chỉ lấy trạng thái=1, loaiDonHang="online"
        return ResponseEntity.ok(donHangService.filterByTrangThaiAndLoai(5, "online"));
    }
    @GetMapping("/donhang/trahanghoantien")
    public ResponseEntity<List<DonHangDTO>> THHT() {
        // Ví dụ: chỉ lấy trạng thái=1, loaiDonHang="online"
        return ResponseEntity.ok(donHangService.filterByTrangThaiAndLoai(6, "online"));
    }

    // Thêm endpoint tìm kiếm đơn hàng POS
    @GetMapping("/donhang/search-pos")
    public ResponseEntity<List<DonHangDTO>> searchDonHangPOS(
            @RequestParam(required = false) String tenKhachHang,
            @RequestParam(required = false) String tuNgay,
            @RequestParam(required = false) String denNgay,
            @RequestParam(defaultValue = "-1") int trangThai) {

        try {
            List<DonHangDTO> donHangs = donHangService.searchDonHangPOS(tenKhachHang, tuNgay, denNgay, trangThai);
            return ResponseEntity.ok(donHangs);
        } catch (Exception e) {
            e.printStackTrace();
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(Collections.emptyList());
        }
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
                request.getEmail(),
                request.getSoDienThoai()
        );
        return updated != null ? ResponseEntity.ok(updated) : ResponseEntity.notFound().build();
    }

    @DeleteMapping("/donhang/delete/{id}")
    public ResponseEntity<Void> delete(@PathVariable Integer id) {
        donHangService.delete(id);
        return ResponseEntity.ok().build();
    }

    // 🛒 1. Tạo đơn hàng online
    @PostMapping("/donhang/online")
    public ResponseEntity<DonHangDTO> taoDon(@RequestBody HoaDonOnlineRequest req) {
        return ResponseEntity.ok(donHangService.taoHoaDonOnline(req));
    }

    // ✅ 2. Xác nhận đơn

    @PutMapping("/donhang/xac-nhan/{id}")
    public ResponseEntity<DonHangDTO> xacNhanDon(@PathVariable Integer id) {
        donHangService.xacNhanDon(id);
        DonHang updated = donHangService.layChiTietDon(id);
        return ResponseEntity.ok(new DonHangDTO(updated));
    }

    // ❌ 3. Hủy đơn
    @PutMapping("/donhang/huy/{id}")
    public ResponseEntity<DonHangDTO> huyDon(@PathVariable Integer id) {
        donHangService.huyDon(id);
        DonHang updated = donHangService.layChiTietDon(id);
        return ResponseEntity.ok(new DonHangDTO(updated));
    }

    // ✏️ 4. Cập nhật địa chỉ + tính phí giao hàng (GHN giả lập)
    @PutMapping("/donhang/sua-dia-chi")
    public ResponseEntity<DonHangDTO> suaDiaChi(@RequestParam Integer id,
                                                @RequestParam String diaChiMoi,
                                                @RequestParam String soDienThoaiMoi,
                                                @RequestParam String tenNguoiNhanMoi,
                                                @RequestParam String emailMoi,
                                                @RequestParam Integer districtId,
                                                @RequestParam String wardCode) {
        DonHangDTO dto = donHangService.capNhatDiaChiVaTinhPhi(
                id, diaChiMoi, soDienThoaiMoi, tenNguoiNhanMoi, emailMoi, districtId, wardCode
        );
        return ResponseEntity.ok(dto);
    }

    // 📜 5. Lịch sử đơn của khách hàng
    @GetMapping("/donhang/khach/{idKhach}")
    public ResponseEntity<List<DonHangDTO>> lichSuKhach(@PathVariable Integer idKhach) {
        List<DonHang> list = donHangService.layDonTheoKhach(idKhach);
        List<DonHangDTO> dtoList = list.stream().map(DonHangDTO::new).toList();
        return ResponseEntity.ok(dtoList);
    }
    // 📦 6. Chi tiết đơn hàng (admin hoặc khách)
    @GetMapping("/donhang/chi-tiet/{id}")
    public ResponseEntity<DonHangDTO> chiTietDon(@PathVariable Integer id) {
        DonHang don = donHangService.layChiTietDon(id);
        return ResponseEntity.ok(new DonHangDTO(don));
    }
    // 🔍 7. Lọc đơn theo trạng thái
    @GetMapping
    public ResponseEntity<List<DonHang>> locTheoTrangThai(@RequestParam Integer trangThai) {
        List<DonHang> list = donHangService.layDonTheoTrangThai(trangThai);
        return ResponseEntity.ok(list);
    }

    // 📊 8. Thống kê đơn hàng
    @GetMapping("/donhang/thong-ke")
    public ResponseEntity<Map<String, Object>> thongKe() {
        Map<String, Object> stats = donHangService.thongKeDon();
        return ResponseEntity.ok(stats);
    }

    @PutMapping("/don-hang/{id}/trang-thai")

    public ResponseEntity<?> doiTrangThai(
            @PathVariable Integer id,
            @RequestParam("value") int value
    ) {
        try {
            TrangThaiDonHang trangThaiMoi = TrangThaiDonHang.fromValue(value);
            donHangService.capNhatTrangThai(id, trangThaiMoi);
            return ResponseEntity.ok("Đã cập nhật trạng thái: " + trangThaiMoi.getDisplayName());
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        }
    }

    // ❌ 9. Giao hàng không thành công
    @PutMapping("/donhang/giao-khong-thanh-cong/{id}")
    public ResponseEntity<DonHangDTO> giaoKhongThanhCong(@PathVariable Integer id) {
        try {
            donHangService.danhDauGiaoKhongThanhCong(id);
            DonHang updated = donHangService.layChiTietDon(id);
            return ResponseEntity.ok(new DonHangDTO(updated));
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(null);
        }
    }


     // Áp dụng voucher cho đơn hàng client online

    @PostMapping("/donhang/{idDonHang}/apply-voucher/{idVoucher}")
    public ResponseEntity<?> applyVoucherToClientOrder(
            @PathVariable Integer idDonHang,
            @PathVariable Integer idVoucher
    ) {
        try {
            // Chỉ áp dụng voucher, KHÔNG trừ số lượng
            DonHangDTO updated = donHangService.applyVoucherForClientOrder(idDonHang, idVoucher);
            if (updated != null) {
                return ResponseEntity.ok().body(updated);
            } else {
                return ResponseEntity.badRequest().body("Không tìm thấy đơn hàng");
            }
        } catch (RuntimeException e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        }
    }

    // API mới để trừ số lượng voucher khi thanh toán thành công
    @PostMapping("/donhang/{idDonHang}/confirm-payment")
    public ResponseEntity<?> confirmPaymentAndDeductVoucher(
            @PathVariable Integer idDonHang
    ) {
        try {
            // Trừ số lượng voucher và cập nhật trạng thái đơn hàng
            DonHangDTO updated = donHangService.confirmPaymentAndDeductVoucher(idDonHang);
            return ResponseEntity.ok().body(updated);
        } catch (RuntimeException e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        }
    }

    @DeleteMapping("/donhang/{idDonHang}/remove-voucher")
    public ResponseEntity<?> removeVoucherFromDonHang(@PathVariable Integer idDonHang) {
        DonHang dh = donHangRepository.findById(idDonHang)
                .orElseThrow(() -> new RuntimeException("Đơn hàng không tồn tại"));

        dh.setGiamGia(null);
        dh.setTongTienGiamGia(dh.getTongTien());

        donHangRepository.save(dh);

        return ResponseEntity.ok("Đã gỡ voucher khỏi đơn hàng");
    }
}


package com.example.backend.service;


import com.example.backend.dto.DonHangDTO;

import com.example.backend.dto.HoaDonOnlineRequest;
import com.example.backend.dto.SanPhamDatDTO;
import com.example.backend.entity.*;
import com.example.backend.enums.TrangThaiDonHang;
import com.example.backend.repository.*;
import jakarta.transaction.Transactional;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.*;
import java.util.stream.Collectors;

@Service
public class DonHangService {


    @Autowired
    private DonHangChiTietRepository donHangChiTietRepository;
    @Autowired
    private DonHangRepository donHangRepository;

    @Autowired
    private NhanVienRepository nhanVienRepository;

    @Autowired
    private KhachHangRepository khachHangRepository;

    @Autowired
    private SanPhamChiTietRepository sanPhamChiTietRepository;

    @Autowired
    private SanPhamChiTietRepository spctRepo;

    @Autowired
    private VoucherRepository voucherRepository;

    @Autowired VoucherService voucherService;

    @Autowired
    private GHNClientService ghnClientService;

    public List<DonHangDTO> getAll() {
        return donHangRepository.findAll()
                .stream()
                .map(this::convertToDTO)
                .collect(Collectors.toList());
    }

    public DonHangDTO getById(int id) {
        return donHangRepository.findById(id)
                .map(this::convertToDTO)
                .orElse(null);
    }

    public DonHangDTO create(DonHangDTO dto) {
        DonHang donHang = convertToEntity(dto);

        // ✅ THÊM: Chỉ set ngayTao khi tạo mới, không set ngayMua
        if (donHang.getNgayTao() == null) {
            donHang.setNgayTao(LocalDate.now());
        }
        // ✅ THÊM: Đảm bảo ngayMua = null khi tạo mới
        donHang.setNgayMua(null);

        // Giữ nguyên phần voucher logic
        if (donHang.getGiamGia() != null) {
            double tongTien = donHang.getTongTien() != null ? donHang.getTongTien() : 0;
            double giam = tinhTienGiamVoucher(tongTien, donHang.getGiamGia());
            donHang.setTongTienGiamGia(giam);
            donHang.setTongTien(tongTien - giam);
        } else {
            donHang.setTongTienGiamGia(0.0);
        }

        return convertToDTO(donHangRepository.save(donHang));
    }


    public DonHangDTO update(int id, DonHangDTO dto) {
        Optional<DonHang> optional = donHangRepository.findById(id);
        if (optional.isPresent()) {
            DonHang donHang = convertToEntity(dto);
            donHang.setId(id);
            return convertToDTO(donHangRepository.save(donHang));
        }
        return null;
    }

    @Transactional
    public DonHangDTO updateVoucher(Integer idDonHang, Integer idgiamGia) {
        Optional<DonHang> optional = donHangRepository.findById(idDonHang);
        if (optional.isPresent()) {
            DonHang donHang = optional.get();

            // Lưu lại voucher cũ
            Voucher oldVoucher = donHang.getGiamGia();

            // Nếu có voucher cũ, cộng lại số lượng
            if (oldVoucher != null) {
                oldVoucher.setSoLuong(oldVoucher.getSoLuong() + 1);
//                System.out.println("Voucher " + oldVoucher.getId() + " soLuong sau khi bo: " + oldVoucher.getSoLuong());

                // Kiểm tra lại trạng thái
                LocalDateTime now = LocalDateTime.now();
                boolean isExpired = oldVoucher.getNgayKetThuc() != null && oldVoucher.getNgayKetThuc().isBefore(now);
                boolean isNotStarted = oldVoucher.getNgayBatDau() != null && oldVoucher.getNgayBatDau().isAfter(now);
                boolean isOutOfStock = oldVoucher.getSoLuong() == null || oldVoucher.getSoLuong() == 0;
                boolean isActive = !isExpired && !isNotStarted && !isOutOfStock;
                if (isActive) {
                    oldVoucher.setTrangThai(1);
                }
                voucherRepository.save(oldVoucher);
            }

            // Nếu có voucher mới
            if (idgiamGia != null) {
                Voucher newVoucher = voucherRepository.findById(idgiamGia).orElse(null);
                if (newVoucher == null) throw new RuntimeException("Không tìm thấy voucher mới");
                if (newVoucher.getSoLuong() <= 0) throw new RuntimeException("Voucher đã hết lượt sử dụng");
                // Trừ số lượng voucher mới
                newVoucher.setSoLuong(newVoucher.getSoLuong() - 1);
                voucherRepository.save(newVoucher);

                voucherService.kiemTraDieuKienVoucher(donHang, idgiamGia);
                donHang.setGiamGia(newVoucher);
            } else {
                donHang.setGiamGia(null);
            }

            capNhatTongTienDonHang(idDonHang);
            return convertToDTO(donHangRepository.save(donHang));
        }
        return null;
    }

    // Hàm tính số tiền giảm giá từ voucher
    private double tinhTienGiamVoucher(double tongTien, Voucher voucher) {
        if (voucher == null) return 0.0;
        double giam = 0.0;
        String loai = voucher.getLoaiVoucher();
        double giaTri = voucher.getGiaTri();

        if ("Giảm giá %".equalsIgnoreCase(loai)) {
            giam = tongTien * giaTri / 100.0;
        } else if ("Giảm giá số tiền".equalsIgnoreCase(loai)) {
            giam = giaTri;
        }
        // Không cho giảm quá tổng tiền
        if (giam > tongTien) giam = tongTien;
        // Làm tròn về số nguyên nếu muốn
        return Math.round(giam);
    }


    public DonHangDTO updateKhachHang(Integer idDonHang, Integer idkhachHang) {
        Optional<DonHang> optional = donHangRepository.findById(idDonHang);
        if (optional.isPresent()) {
            DonHang donHang = optional.get();
            if (idkhachHang != null) {
                Optional<KhachHang> kh = khachHangRepository.findById(idkhachHang);
                kh.ifPresent(donHang::setKhachHang);
            } else {
                donHang.setKhachHang(null);
            }
            return convertToDTO(donHangRepository.save(donHang));
        }
        return null;
    }

    @Transactional
    public void delete(Integer id) {
        Optional<DonHang> donHangOptional = donHangRepository.findById(id);
        if (donHangOptional.isPresent()) {
            DonHang donHang = donHangOptional.get();
            // Duyệt và cộng lại tồn kho TRƯỚC khi clear hoặc xóa
            for (DonHangChiTiet chiTiet : donHang.getDonHangChiTiets()) {
                SanPhamChiTiet spct = chiTiet.getSanPhamChiTiet();
                if (spct != null) {
                    spct.setSoLuong(spct.getSoLuong() + chiTiet.getSoLuong());
                    sanPhamChiTietRepository.save(spct);
                }
            }
            // KHÔNG cần clear() nữa, chỉ cần xóa đơn hàng, Hibernate sẽ tự xóa chi tiết (do orphanRemoval = true)
            donHangRepository.delete(donHang);
        }
    }

    // Service
    public List<DonHangDTO> filterByTrangThaiAndLoai(Integer trangThai, String loaiDonHang) {
        return donHangRepository.findByTrangThaiAndLoaiDonHang(trangThai, loaiDonHang)
                .stream()
                .map(DonHangDTO::new)   // <-- dùng constructor, đã map ghiChu
                .collect(Collectors.toList());
    }
    public List<DonHangDTO> filterByTrangThaiAndLoaiAndKhachHang(Integer trangThai, String loaiDonHang, Integer idKhachHang) {
        return donHangRepository.findByTrangThaiAndLoaiDonHangAndKhachHang(trangThai, loaiDonHang, idKhachHang)
                .stream()
                .map(this::convertToDTO)
                .collect(Collectors.toList());
    }


    public DonHangDTO xacNhanDonHang(
            Integer id,
            Double tongTien,
            Integer idkhachHang,
            String tenKhachHang,
            String email,
            String soDienThoai
    ) {
        Optional<DonHang> optional = donHangRepository.findById(id);
        if (optional.isPresent()) {
            DonHang donHang = optional.get();

            // ✅ SỬA: Bỏ validation voucher khi thanh toán (đã được validate khi áp dụng)
            // if (donHang.getGiamGia() != null) {
            //     try {
            //         voucherService.kiemTraDieuKienVoucher(donHang, donHang.getGiamGia().getId());
            //     } catch (Exception e) {
            //         // ... reset voucher ...
            //     }
            // }

            donHang.setTrangThai(1); // Đã thanh toán
            donHang.setNgayMua(LocalDate.now());

//             ✅ SỬA: Không gọi capNhatTongTienDonHang() để giữ nguyên voucher
//             capNhatTongTienDonHang(donHang.getId());

            // ... phần còn lại giữ nguyên
            KhachHang khachHang = null;
            if (idkhachHang != null) {
                khachHang = khachHangRepository.findById(idkhachHang).orElse(null);
            } else if (tenKhachHang != null && !tenKhachHang.isEmpty()) {
                khachHang = new KhachHang();
                khachHang.setTenKhachHang(tenKhachHang);
                khachHang.setEmail(email);
                khachHang.setSoDienThoai(soDienThoai);
                khachHang = khachHangRepository.save(khachHang);
            }
            if (khachHang != null) {
                donHang.setKhachHang(khachHang);
            }

            donHang = donHangRepository.save(donHang);
            return convertToDTO(donHang);
        }
        return null;
    }

    private DonHangDTO convertToDTO(DonHang dh) {
        DonHangDTO dto = new DonHangDTO();
        dto.setId(dh.getId());
        dto.setIdkhachHang(dh.getKhachHang() != null ? dh.getKhachHang().getId() : null);
        dto.setIdnhanVien(dh.getNhanVien() != null ? dh.getNhanVien().getId() : null);
        dto.setTenNhanVien(dh.getNhanVien() != null ? dh.getNhanVien().getTenNhanVien() : null); // Thêm dòng này
        dto.setIdgiamGia(dh.getGiamGia() != null ? dh.getGiamGia().getId() : null);
        dto.setNgayMua(dh.getNgayMua());
        dto.setNgayTao(dh.getNgayTao());
        dto.setLoaiDonHang(dh.getLoaiDonHang());
        dto.setTrangThai(dh.getTrangThai());
        dto.setTongTien(dh.getTongTien());
        dto.setTongTienGiamGia(dh.getTongTienGiamGia());
        dto.setDiaChiGiaoHang(dh.getDiaChiGiaoHang());
        dto.setSoDienThoaiGiaoHang(dh.getSoDienThoaiGiaoHang());
        dto.setEmailGiaoHang(dh.getEmailGiaoHang());
        dto.setTenNguoiNhan(dh.getTenNguoiNhan());
        dto.setGhiChu(dh.getGhiChu());
        return dto;
    }

    private DonHangDTO convertToDTOOnline(DonHang dh) {
        DonHangDTO dto = new DonHangDTO();
        dto.setId(dh.getId());
        dto.setIdkhachHang(dh.getKhachHang() != null ? dh.getKhachHang().getId() : null);
        dto.setIdnhanVien(dh.getNhanVien() != null ? dh.getNhanVien().getId() : null);
        dto.setTenNhanVien(dh.getNhanVien() != null ? dh.getNhanVien().getTenNhanVien() : null);
        dto.setIdgiamGia(dh.getGiamGia() != null ? dh.getGiamGia().getId() : null);
        dto.setNgayMua(dh.getNgayMua());
        dto.setNgayTao(dh.getNgayTao());
        dto.setLoaiDonHang(dh.getLoaiDonHang());
        dto.setTrangThai(dh.getTrangThai());
        dto.setTongTien(dh.getTongTien());
        dto.setTongTienGiamGia(dh.getTongTienGiamGia());
        dto.setDiaChiGiaoHang(dh.getDiaChiGiaoHang());
        dto.setSoDienThoaiGiaoHang(dh.getSoDienThoaiGiaoHang());
        dto.setEmailGiaoHang(dh.getEmailGiaoHang());
        dto.setTenNguoiNhan(dh.getTenNguoiNhan());

        // ✅ THÊM: Mapping cho phiVanChuyen
        dto.setPhiVanChuyen(dh.getPhiVanChuyen());

        return dto;
    }

    private DonHang convertToEntity(DonHangDTO dto) {
        DonHang dh = new DonHang();

        // ✅ SỬA: Chỉ set ngayTao nếu có từ DTO
        if (dto.getNgayTao() != null) {
            dh.setNgayTao(dto.getNgayTao());
        }
        // ✅ THÊM: Đảm bảo ngayMua = null khi tạo mới
        dh.setNgayMua(null);

        // Giữ nguyên phần còn lại
        dh.setLoaiDonHang(dto.getLoaiDonHang());
        dh.setTrangThai(dto.getTrangThai());
        dh.setTongTien(dto.getTongTien());
        dh.setTongTienGiamGia(dto.getTongTienGiamGia());

        if (dto.getIdnhanVien() != null) {
            Optional<NhanVien> nv = nhanVienRepository.findById(dto.getIdnhanVien());
            nv.ifPresent(dh::setNhanVien);
        }

        if (dto.getIdkhachHang() != null) {
            Optional<KhachHang> kh = khachHangRepository.findById(dto.getIdkhachHang());
            kh.ifPresent(dh::setKhachHang);
        }

        if (dto.getIdgiamGia() != null) {
            voucherService.kiemTraDieuKienVoucher(dh, dto.getIdgiamGia());
            Optional<Voucher> voucher = voucherRepository.findById(dto.getIdgiamGia());
            voucher.ifPresent(dh::setGiamGia);
        }

        return dh;
    }

    private DonHang convertToEntityOnline(DonHangDTO dto) {
        DonHang dh = new DonHang();

        // ✅ SỬA: Chỉ set ngayTao nếu có từ DTO
        if (dto.getNgayTao() != null) {
            dh.setNgayTao(dto.getNgayTao());
        }

        // ✅ THÊM: Đảm bảo ngayMua = null khi tạo mới
        dh.setNgayMua(null);

        // Giữ nguyên phần còn lại
        dh.setLoaiDonHang(dto.getLoaiDonHang());
        dh.setTrangThai(dto.getTrangThai());

        // ✅ SỬA: Luôn set tongTien và phiVanChuyen trước
        dh.setTongTien(dto.getTongTien());
        dh.setPhiVanChuyen(dto.getPhiVanChuyen());

        // ✅ THÊM: Mapping cho tongTienGiamGia
        dh.setTongTienGiamGia(dto.getTongTienGiamGia());

        // ✅ THÊM: Mapping cho các trường khác
        dh.setDiaChiGiaoHang(dto.getDiaChiGiaoHang());
        dh.setSoDienThoaiGiaoHang(dto.getSoDienThoaiGiaoHang());
        dh.setEmailGiaoHang(dto.getEmailGiaoHang());
        dh.setTenNguoiNhan(dto.getTenNguoiNhan());

        if (dto.getIdnhanVien() != null) {
            Optional<NhanVien> nv = nhanVienRepository.findById(dto.getIdnhanVien());
            nv.ifPresent(dh::setNhanVien);
        }

        if (dto.getIdkhachHang() != null) {
            Optional<KhachHang> kh = khachHangRepository.findById(dto.getIdkhachHang());
            kh.ifPresent(dh::setKhachHang);
        }

        // chỉ set voucher nếu có
        if (dto.getIdgiamGia() != null) {
            Optional<Voucher> voucher = voucherRepository.findById(dto.getIdgiamGia());
            voucher.ifPresent(dh::setGiamGia);
        }
        // Không có voucher: tongTien đã được set ở trên rồi

        return dh;
    }


        @Transactional
        public DonHangDTO capNhatTongTienPhiShip(Integer idDonHang) {
            Optional<DonHang> optional = donHangRepository.findById(idDonHang);
            if (optional.isPresent()) {
                DonHang donHang = optional.get();

                // Bước 1: Lấy chi tiết sản phẩm và tính tổng tiền sản phẩm
                List<DonHangChiTiet> chiTiets = donHangChiTietRepository.findByDonHang_Id(idDonHang);
                double tongTienSanPham = chiTiets.stream()
                        .mapToDouble(DonHangChiTiet::getThanhTien)
                        .sum();

                // Bước 2: Lấy phí vận chuyển từ đơn hàng
                double phiVanChuyen = donHang.getPhiVanChuyen() != null ? donHang.getPhiVanChuyen() : 0.0;

                // Bước 3: Tính tổng tiền cuối cùng
                double tongTienCuoiCung = tongTienSanPham + phiVanChuyen;

                // Bước 4: Cập nhật vào database
                donHang.setTongTien(tongTienCuoiCung);

                // Bước 5: Lưu vào database
                donHang = donHangRepository.save(donHang);

                // Log để kiểm tra
                System.out.println("✅ Đã cập nhật tổng tiền đơn hàng #" + idDonHang);
                System.out.println("   - Tổng tiền sản phẩm: " + tongTienSanPham);
                System.out.println("   - Phí vận chuyển: " + phiVanChuyen);
                System.out.println("   - Tổng tiền cuối cùng: " + tongTienCuoiCung);

                return convertToDTO(donHang);
            }
            return null;
        }


    public void capNhatTongTienDonHang(Integer idDonHang) {
        DonHang donHang = donHangRepository.findById(idDonHang).orElseThrow();
        List<DonHangChiTiet> chiTiets = donHangChiTietRepository.findByDonHang_Id(idDonHang);
        double tongTienGoc = 0;
        for (DonHangChiTiet ct : chiTiets) {
            tongTienGoc += ct.getThanhTien();
        }

        double giam = 0.0;
        Voucher voucher = donHang.getGiamGia();
        if (voucher != null) {
            // Kiểm tra điều kiện đơn tối thiểu
            if (tongTienGoc < voucher.getDonToiThieu()) {
                // Không đủ điều kiện, hủy voucher
                donHang.setGiamGia(null);
                donHang.setTongTienGiamGia(0.0);
            } else {
                giam = tinhTienGiamVoucher(tongTienGoc, voucher);
                donHang.setTongTienGiamGia(giam);
            }
        } else {
            donHang.setTongTienGiamGia(0.0);
        }
        donHang.setTongTien(tongTienGoc - giam);

        donHangRepository.save(donHang);
    }
    public void capNhatTongTienDonHang2(Integer idDonHang) {
        DonHang donHang = donHangRepository.findById(idDonHang).orElseThrow();
        List<DonHangChiTiet> chiTiets = donHangChiTietRepository.findByDonHang_Id(idDonHang);

        double tongTienGoc = 0;
        for (DonHangChiTiet ct : chiTiets) {
            tongTienGoc += ct.getThanhTien();
        }

        double giam = 0.0;
        Voucher voucher = donHang.getGiamGia();
        if (voucher != null) {
            if (tongTienGoc < voucher.getDonToiThieu()) {
                donHang.setGiamGia(null);
                donHang.setTongTienGiamGia(0.0);
            } else {
                giam = tinhTienGiamVoucher(tongTienGoc, voucher);
                donHang.setTongTienGiamGia(giam);
            }
        } else {
            donHang.setTongTienGiamGia(0.0);
        }

        // ✅ SỬA: Cộng thêm phiVanChuyen
        double phiVanChuyen = donHang.getPhiVanChuyen() != null ? donHang.getPhiVanChuyen() : 0.0;
        donHang.setTongTien(tongTienGoc - giam + phiVanChuyen);

        donHangRepository.save(donHang);
    }

    // Tạo đơn mới
    public DonHangDTO taoHoaDonOnline(HoaDonOnlineRequest req) {
        DonHang don = new DonHang();
        don.setNgayTao(LocalDate.now());
        don.setLoaiDonHang("ONLINE");
        don.setTrangThai(TrangThaiDonHang.CHO_XAC_NHAN.getValue());
        don.setDiaChiGiaoHang(req.getDiaChiGiaoHang());
        don.setSoDienThoaiGiaoHang(req.getSoDienThoaiGiaoHang());
        don.setEmailGiaoHang(req.getEmailGiaoHang());
        don.setTenNguoiNhan(req.getTenNguoiNhan());
        don.setKhachHang(khachHangRepository.findById(req.getIdKhachHang()).orElse(null));
        don = donHangRepository.save(don);

        double tongTien = 0;
        List<DonHangChiTiet> dsChiTiet = new ArrayList<>();

        for (SanPhamDatDTO dto : req.getSanPhamDat()) {
            SanPhamChiTiet sp = spctRepo.findById(dto.getIdSanPhamChiTiet()).orElseThrow();
            if (sp.getSoLuong() < dto.getSoLuong())
                throw new RuntimeException("Sản phẩm đã hết hàng");

            sp.setSoLuong(sp.getSoLuong() - dto.getSoLuong());
            spctRepo.save(sp);

            DonHangChiTiet ct = new DonHangChiTiet();
            ct.setDonHang(don);
            ct.setSanPhamChiTiet(sp);
            ct.setSoLuong(dto.getSoLuong());
            ct.setGia(sp.getGiaBan());
            ct.setThanhTien(dto.getSoLuong() * sp.getGiaBan());
            dsChiTiet.add(donHangChiTietRepository.save(ct));
            tongTien += ct.getThanhTien();
        }

        don.setDonHangChiTiets(dsChiTiet);
        don.setTongTien(tongTien);

        if (req.getIdVoucher() != null) {
            Voucher v = voucherRepository.findById(req.getIdVoucher()).orElse(null);
            if (v != null) {
                double giam = "TIEN".equalsIgnoreCase(v.getLoaiVoucher())
                        ? v.getGiaTri()
                        : tongTien * v.getGiaTri() / 100.0;

                don.setGiamGia(v);
                don.setTongTienGiamGia(giam);
                don.setTongTien(tongTien - giam);
            }
        }

        don = donHangRepository.save(don);
        return new DonHangDTO(don);
    }





    public void xacNhanDon(Integer id) {
        DonHang d = donHangRepository.findById(id).orElseThrow();

        // ✅ THÊM: Kiểm tra tồn kho trước khi xác nhận
        try {
            kiemTraTonKho(d);
        } catch (Exception e) {
            throw new RuntimeException("Không thể xác nhận đơn hàng: " + e.getMessage());
        }

        d.setTrangThai(TrangThaiDonHang.XAC_NHAN.getValue());
        d.setNgayMua(LocalDate.now());
        donHangRepository.save(d);

        // ✅ THÊM: Trừ số lượng tồn kho sản phẩm
        trutonKhoSanPham(d);
    }

    // ✅ THÊM: Method kiểm tra tồn kho trước khi xác nhận
    private void kiemTraTonKho(DonHang donHang) throws Exception {
        if (donHang.getDonHangChiTiets() == null || donHang.getDonHangChiTiets().isEmpty()) {
            throw new Exception("Đơn hàng không có chi tiết sản phẩm");
        }

        System.out.println("🔍 Bắt đầu kiểm tra tồn kho cho đơn hàng: " + donHang.getId());

        List<String> loiTonKho = new ArrayList<>();

        for (DonHangChiTiet chiTiet : donHang.getDonHangChiTiets()) {
            try {
                // Lấy sản phẩm chi tiết từ DB
                SanPhamChiTiet spct = sanPhamChiTietRepository.findById(chiTiet.getSanPhamChiTiet().getId())
                        .orElse(null);

                if (spct != null) {
                    System.out.println("�� Kiểm tra sản phẩm: " + spct.getSanPham().getTenSanPham());
                    System.out.println("�� Kích thước: " + spct.getKichThuoc().getTenKichThuoc());
                    System.out.println("🎨 Màu sắc: " + spct.getMauSac().getTenMauSac());
                    System.out.println("📊 Tồn kho: " + spct.getSoLuong() + " | Cần: " + chiTiet.getSoLuong());

                    // Kiểm tra số lượng tồn kho có đủ không
                    if (spct.getSoLuong() < chiTiet.getSoLuong()) {
                        String loi = String.format("Sản phẩm '%s' (KT: %s, Màu: %s) - Tồn kho: %d, Cần: %d",
                                spct.getSanPham().getTenSanPham(),
                                spct.getKichThuoc().getTenKichThuoc(),
                                spct.getMauSac().getTenMauSac(),
                                spct.getSoLuong(),
                                chiTiet.getSoLuong());
                        loiTonKho.add(loi);

                        System.out.println("⚠️ CẢNH BÁO: Số lượng tồn kho không đủ!");
                    } else {
                        System.out.println("✅ Tồn kho đủ");
                    }
                } else {
                    String loi = "Không tìm thấy sản phẩm chi tiết với ID: " + chiTiet.getSanPhamChiTiet().getId();
                    loiTonKho.add(loi);
                    System.out.println("❌ " + loi);
                }

                System.out.println("---");

            } catch (Exception e) {
                String loi = "Lỗi khi kiểm tra sản phẩm: " + e.getMessage();
                loiTonKho.add(loi);
                System.err.println("❌ " + loi);
            }
        }

        // Nếu có lỗi tồn kho, throw exception
        if (!loiTonKho.isEmpty()) {
            String message = "Số lượng tồn kho không đủ:\n" + String.join("\n", loiTonKho);
            System.err.println("❌ " + message);
            throw new Exception(message);
        }

        System.out.println("✅ Kiểm tra tồn kho hoàn thành - Tất cả sản phẩm đều đủ số lượng!");
    }

    // ✅ THÊM: Method trừ tồn kho sản phẩm (cải tiến)
    private void trutonKhoSanPham(DonHang donHang) {
        if (donHang.getDonHangChiTiets() != null && !donHang.getDonHangChiTiets().isEmpty()) {
            System.out.println("🔄 Bắt đầu trừ tồn kho sản phẩm cho đơn hàng: " + donHang.getId());

            for (DonHangChiTiet chiTiet : donHang.getDonHangChiTiets()) {
                try {
                    // Lấy sản phẩm chi tiết từ DB
                    SanPhamChiTiet spct = sanPhamChiTietRepository.findById(chiTiet.getSanPhamChiTiet().getId())
                            .orElse(null);

                    if (spct != null) {
                        System.out.println("�� Sản phẩm: " + spct.getSanPham().getTenSanPham());
                        System.out.println("�� Kích thước: " + spct.getKichThuoc().getTenKichThuoc());
                        System.out.println("🎨 Màu sắc: " + spct.getMauSac().getTenMauSac());
                        System.out.println("📊 Số lượng tồn kho hiện tại: " + spct.getSoLuong());
                        System.out.println("�� Số lượng đã bán: " + chiTiet.getSoLuong());

                        // Kiểm tra lại một lần nữa để đảm bảo an toàn
                        if (spct.getSoLuong() >= chiTiet.getSoLuong()) {
                            // Trừ số lượng tồn kho
                            int soLuongCu = spct.getSoLuong();
                            spct.setSoLuong(spct.getSoLuong() - chiTiet.getSoLuong());

                            // Lưu lại sản phẩm chi tiết
                            SanPhamChiTiet savedSpct = sanPhamChiTietRepository.save(spct);

                            System.out.println("✅ Đã trừ tồn kho thành công!");
                            System.out.println("📊 Số lượng cũ: " + soLuongCu + " → Mới: " + savedSpct.getSoLuong());
                        } else {
                            System.err.println("❌ LỖI: Số lượng tồn kho không đủ khi trừ tồn kho!");
                            System.err.println("📊 Tồn kho: " + spct.getSoLuong() + " < Đã bán: " + chiTiet.getSoLuong());
                            throw new RuntimeException("Số lượng tồn kho không đủ khi trừ tồn kho");
                        }
                    } else {
                        System.err.println("❌ Không tìm thấy sản phẩm chi tiết với ID: " + chiTiet.getSanPhamChiTiet().getId());
                        throw new RuntimeException("Không tìm thấy sản phẩm chi tiết");
                    }

                    System.out.println("---");

                } catch (Exception e) {
                    System.err.println("❌ Lỗi khi trừ tồn kho sản phẩm: " + e.getMessage());
                    throw new RuntimeException("Lỗi khi trừ tồn kho: " + e.getMessage());
                }
            }

            System.out.println("✅ Hoàn thành trừ tồn kho sản phẩm!");
        } else {
            System.out.println("ℹ️ Đơn hàng không có chi tiết sản phẩm");
        }
    }


//    public void huyDon(Integer idDon, String ghiChu) {
//        DonHang don = donHangRepository.findById(idDon)
//                .orElseThrow(() -> new RuntimeException("Không tìm thấy đơn"));
//
//        int trangThaiCu = don.getTrangThai();
//
//        // Kiểm tra trạng thái có được phép hủy
//        List<Integer> trangThaiDuocHuy = List.of(0, 1, 2, 3); // Được hủy nếu chưa giao
//        if (!trangThaiDuocHuy.contains(trangThaiCu)) {
//            throw new RuntimeException("Không thể hủy đơn ở trạng thái: "
//                    + TrangThaiDonHang.fromValue(trangThaiCu).getDisplayName());
//        }
//
//        // Lưu trạng thái trước khi hủy
//        don.setTrangThaiTruocKhiHuy(trangThaiCu);
//
//        // Cập nhật trạng thái đơn
//        don.setTrangThai(TrangThaiDonHang.DA_HUY.getValue());
//
//        // ✅ THÊM: Lưu lý do hủy
//        don.setGhiChu(ghiChu);
//
//        // Hoàn lại số lượng sản phẩm
//        for (DonHangChiTiet ct : don.getDonHangChiTiets()) {
//            SanPhamChiTiet sp = ct.getSanPhamChiTiet();
//            if (sp != null) {
//                int hienTai = sp.getSoLuong();
//                sp.setSoLuong(hienTai + ct.getSoLuong());
//                spctRepo.save(sp);
//            }
//        }
//        donHangRepository.save(don);
//    }

    public void huyDon(Integer idDon, String ghiChu) {
        DonHang don = donHangRepository.findById(idDon)
                .orElseThrow(() -> new RuntimeException("Không tìm thấy đơn"));

        int trangThaiCu = don.getTrangThai();

        // Kiểm tra trạng thái có được phép hủy
        List<Integer> trangThaiDuocHuy = List.of(0, 1, 2, 3); // Được hủy nếu chưa giao
        if (!trangThaiDuocHuy.contains(trangThaiCu)) {
            throw new RuntimeException("Không thể hủy đơn ở trạng thái: "
                    + TrangThaiDonHang.fromValue(trangThaiCu).getDisplayName());
        }

        // Lưu trạng thái trước khi hủy
        don.setTrangThaiTruocKhiHuy(trangThaiCu);

        // Cập nhật trạng thái đơn
        don.setTrangThai(TrangThaiDonHang.DA_HUY.getValue());

        // ✅ THÊM: Lưu lý do hủy
        don.setGhiChu(ghiChu);

        // ✅ SỬA: Hoàn lại voucher nếu đơn hàng có sử dụng (CHỈ hoàn số lượng, KHÔNG reset voucher)
        if (don.getGiamGia() != null) {
            Voucher voucher = don.getGiamGia();
            // Hoàn lại số lượng voucher
            voucher.setSoLuong(voucher.getSoLuong() + 1);
            voucherRepository.save(voucher);



            System.out.println("✅ Đã hoàn lại voucher: " + voucher.getTenVoucher());
            System.out.println("📊 Số lượng voucher: " + (voucher.getSoLuong() - 1) + " → " + voucher.getSoLuong());
        }

        // ✅ SỬA: Chỉ hoàn lại tồn kho cho trạng thái 1, 2, 3
        if (trangThaiCu != 0) {
            System.out.println("🔄 Đơn hàng trạng thái " + trangThaiCu + " - Bắt đầu hoàn lại tồn kho...");

            // Hoàn lại số lượng sản phẩm
            for (DonHangChiTiet ct : don.getDonHangChiTiets()) {
                SanPhamChiTiet sp = ct.getSanPhamChiTiet();
                if (sp != null) {
                    int soLuongCu = sp.getSoLuong();
                    int soLuongHoanLai = ct.getSoLuong();

                    // Hoàn lại tồn kho
                    sp.setSoLuong(soLuongCu + soLuongHoanLai);
                    spctRepo.save(sp);

                    System.out.println("✅ Đã hoàn lại tồn kho sản phẩm: " + sp.getSanPham().getTenSanPham());
                    System.out.println("📊 Số lượng cũ: " + soLuongCu + " → Mới: " + sp.getSoLuong());
                    System.out.println("🔄 Hoàn lại: +" + soLuongHoanLai);
                }
            }

            System.out.println("✅ Hoàn thành hoàn lại tồn kho!");
        } else {
            System.out.println("ℹ️ Đơn hàng trạng thái 0 (chờ thanh toán) - KHÔNG hoàn lại tồn kho");
        }

        donHangRepository.save(don);

        System.out.println("✅ Hoàn thành hủy đơn hàng #" + idDon);
        System.out.println("�� Lý do hủy: " + ghiChu);
        System.out.println("🔄 Trạng thái: " + trangThaiCu + " → " + don.getTrangThai());
    }


    // Cập nhật địa chỉ & phí giao hàng
    public DonHangDTO capNhatDiaChiVaTinhPhi(
            Integer id,
            String diaChiMoi,           // ← Địa chỉ từ frontend (đã có đầy đủ thông tin)
            String sdtMoi,
            String tenNguoiNhanMoi,
            String emailMoi,
            Integer districtId,
            String wardCode,
            Integer phiVanChuyenMoi
    ) {
        DonHang don = donHangRepository.findById(id).orElseThrow();

        // ✅ Cập nhật thông tin giao hàng
        don.setDiaChiGiaoHang(diaChiMoi);  // ← CHỈ LƯU ĐỊA CHỈ TỪ FRONTEND
        don.setSoDienThoaiGiaoHang(sdtMoi);
        don.setTenNguoiNhan(tenNguoiNhanMoi);
        don.setEmailGiaoHang(emailMoi);

        // ✅ Sử dụng phí ship từ frontend nếu có
        if (phiVanChuyenMoi != null && phiVanChuyenMoi > 0) {
            don.setPhiVanChuyen(phiVanChuyenMoi);
            System.out.println("✅ Sử dụng phí ship từ frontend: " + phiVanChuyenMoi);
        } else {
            int phiVanChuyen = ghnClientService.tinhPhiVanChuyen(districtId, wardCode, 3000);
            don.setPhiVanChuyen(phiVanChuyen);
            System.out.println("⚠️ Tính lại phí ship: " + phiVanChuyen);
        }

        donHangRepository.save(don);
        capNhatTongTienDonHang2(id);

        DonHangDTO dto = new DonHangDTO(don);
        return dto;
    }
    private int tinhPhiGHN(int districtId, String wardCode) {
        return 30000; // giả lập
    }

    public List<DonHang> layDonTheoKhach(Integer idKhach) {
        try {
            if (idKhach == null) {
                throw new RuntimeException("ID khách hàng không được null");
            }

            // ✅ THAY ĐỔI: Chỉ lấy đơn hàng Online
            List<DonHang> donHangs = donHangRepository.findByKhachHangIdAndLoaiDonHangOnlineWithChiTiet(idKhach);

            // Log để debug
            System.out.println("Tìm thấy " + donHangs.size() + " đơn hàng Online cho khách hàng ID: " + idKhach);

            return donHangs;
        } catch (Exception e) {
            System.err.println("Lỗi khi lấy đơn hàng Online theo khách hàng: " + e.getMessage());
            throw new RuntimeException("Không thể lấy danh sách đơn hàng Online: " + e.getMessage());
        }
    }

    public DonHang layChiTietDon(Integer id) {
        DonHang don = donHangRepository.findWithChiTiet(id);
        if (don == null) throw new RuntimeException("Không tìm thấy đơn #" + id);
        return don;
    }

    public List<DonHangDTO> getByTrangThaiDTO(Integer trangThai) {
        return donHangRepository.findByTrangThai(trangThai)
                .stream()
                .map(this::convertToDTO)
                .collect(Collectors.toList());
    }

    public List<DonHang> getByTrangThai(Integer trangThai) {
        return donHangRepository.findByTrangThai(trangThai);
    }

    public Map<String, Object> thongKeDon() {
        long tong = donHangRepository.count();
        double doanhThu = donHangRepository.sumTongTien();
        int daGiao = donHangRepository.countByTrangThai(TrangThaiDonHang.DA_GIAO.getValue()); // ✅ Dùng số thay vì chữ


        return Map.of(
                "tongDon", tong,
                "doanhThu", doanhThu,
                "donDaGiao", daGiao
        );
    }
    public List<DonHang> layDonTheoTrangThai(Integer trangThai) {
        return donHangRepository.findByTrangThai(trangThai);
    }


    public void capNhatTrangThai(Integer idDon, TrangThaiDonHang trangThaiMoi) {
        DonHang don = donHangRepository.findById(idDon)
                .orElseThrow(() -> new RuntimeException("Không tìm thấy đơn hàng"));

        TrangThaiDonHang hienTai = TrangThaiDonHang.fromValue(don.getTrangThai());
        if (!isTrangThaiHopLe(hienTai, trangThaiMoi)) {
            throw new RuntimeException("Không thể chuyển từ "
                    + hienTai.getDisplayName() + " sang "
                    + trangThaiMoi.getDisplayName());
        }

        if (trangThaiMoi == TrangThaiDonHang.DA_GIAO) {
            don.setNgayMua(LocalDate.now());
        }

        don.setTrangThai(trangThaiMoi.getValue());
        donHangRepository.save(don);
    }

    public void danhDauGiaoKhongThanhCong(Integer idDon, String ghiChu) {
        DonHang don = donHangRepository.findById(idDon)
                .orElseThrow(() -> new RuntimeException("Không tìm thấy đơn hàng"));

        TrangThaiDonHang hienTai = TrangThaiDonHang.fromValue(don.getTrangThai());
        if (hienTai != TrangThaiDonHang.DANG_GIAO) {
            throw new RuntimeException("Chỉ có thể đánh dấu giao không thành công khi đơn đang giao");
        }

        // Lưu trạng thái trước khi giao hàng không thành công
        don.setTrangThaiTruocKhiHuy(don.getTrangThai());

        // Cập nhật trạng thái đơn
        don.setTrangThai(TrangThaiDonHang.GIAO_HANG_KHONG_THANH_CONG.getValue());

        // ✅ THÊM: Lưu lý do giao hàng không thành công
        don.setGhiChu(ghiChu);

        // ✅ SỬA: Hoàn lại voucher nếu đơn hàng có sử dụng (CHỈ hoàn số lượng, KHÔNG reset voucher)
        if (don.getGiamGia() != null) {
            Voucher voucher = don.getGiamGia();
            // Hoàn lại số lượng voucher
            voucher.setSoLuong(voucher.getSoLuong() + 1);
            voucherRepository.save(voucher);

            System.out.println("✅ Đã hoàn lại voucher: " + voucher.getTenVoucher());
            System.out.println("📊 Số lượng voucher: " + (voucher.getSoLuong() - 1) + " → " + voucher.getSoLuong());
        }

        // ✅ SỬA: Hoàn lại tồn kho vì đơn hàng đã được xác nhận và trừ tồn kho
        System.out.println("�� Đơn hàng giao hàng không thành công - Bắt đầu hoàn lại tồn kho...");

        for (DonHangChiTiet ct : don.getDonHangChiTiets()) {
            SanPhamChiTiet sp = ct.getSanPhamChiTiet();
            if (sp != null) {
                int soLuongCu = sp.getSoLuong();
                int soLuongHoanLai = ct.getSoLuong();

                // Hoàn lại tồn kho
                sp.setSoLuong(soLuongCu + soLuongHoanLai);
                spctRepo.save(sp);

                System.out.println("✅ Đã hoàn lại tồn kho sản phẩm: " + sp.getSanPham().getTenSanPham());
                System.out.println("📊 Số lượng cũ: " + soLuongCu + " → Mới: " + sp.getSoLuong());
                System.out.println("🔄 Hoàn lại: +" + soLuongHoanLai);
            }
        }

        System.out.println("✅ Hoàn thành hoàn lại tồn kho!");

        donHangRepository.save(don);

        System.out.println("✅ Hoàn thành đánh dấu giao hàng không thành công cho đơn hàng #" + idDon);
        System.out.println(" Lý do: " + ghiChu);
        System.out.println("🔄 Trạng thái: " + hienTai.getValue() + " → " + don.getTrangThai());
    }

    private boolean isTrangThaiHopLe(TrangThaiDonHang hienTai, TrangThaiDonHang moi) {
        return switch (hienTai) {
            case CHO_XAC_NHAN -> moi == TrangThaiDonHang.XAC_NHAN || moi == TrangThaiDonHang.DA_HUY;
            case XAC_NHAN -> moi == TrangThaiDonHang.DANG_CHUAN_BI || moi == TrangThaiDonHang.DA_HUY;
            case DANG_CHUAN_BI -> moi == TrangThaiDonHang.DANG_GIAO || moi == TrangThaiDonHang.DA_HUY;
            case DANG_GIAO -> moi == TrangThaiDonHang.DA_GIAO || moi == TrangThaiDonHang.DA_HUY || moi == TrangThaiDonHang.GIAO_HANG_KHONG_THANH_CONG;
            case DA_GIAO -> moi == TrangThaiDonHang.TRA_HANG_HOAN_TIEN;
            default -> false;
        };
    }

    public List<DonHangDTO> searchDonHangOnline(String searchText, String tuNgay, String denNgay) {
        try {
            // Sửa lại: "Online" thay vì "Đặt hàng online"
            List<DonHang> allDonHangOnline = donHangRepository.findByTrangThaiAndLoaiDonHang(null, "Online");



            if (allDonHangOnline.isEmpty()) {
                return Collections.emptyList();
            }

            // Log một vài đơn hàng để kiểm tra
            allDonHangOnline.stream().limit(3).forEach(dh -> {

            });

            // Lọc theo các điều kiện
            return allDonHangOnline.stream()
                    .filter(dh -> {
                        // Lọc theo searchText (tên khách hàng HOẶC số điện thoại)
                        if (searchText != null && !searchText.trim().isEmpty()) {
                            String search = searchText.toLowerCase().trim();
                            String tenNguoiNhan = dh.getTenNguoiNhan() != null ? dh.getTenNguoiNhan().toLowerCase() : "";
                            String soDienThoai = dh.getSoDienThoaiGiaoHang() != null ? dh.getSoDienThoaiGiaoHang() : "";

                            // Tìm kiếm trong cả tên và số điện thoại
                            if (!tenNguoiNhan.contains(search) && !soDienThoai.contains(search)) {
                                return false;
                            }
                        }

                        // Lọc theo khoảng ngày (ngày tạo)
                        if (tuNgay != null && !tuNgay.trim().isEmpty()) {
                            try {
                                LocalDate tuNgayDate = LocalDate.parse(tuNgay);
                                LocalDate ngayTaoDate = dh.getNgayTao();
                                if (ngayTaoDate == null || ngayTaoDate.isBefore(tuNgayDate)) {
                                    return false;
                                }
                            } catch (Exception e) {
                                // Nếu parse lỗi thì bỏ qua điều kiện này
                            }
                        }

                        if (denNgay != null && !denNgay.trim().isEmpty()) {
                            try {
                                LocalDate denNgayDate = LocalDate.parse(denNgay);
                                LocalDate ngayTaoDate = dh.getNgayTao();
                                if (ngayTaoDate == null || ngayTaoDate.isAfter(denNgayDate)) {
                                    return false;
                                }
                            } catch (Exception e) {
                                // Nếu parse lỗi thì bỏ qua điều kiện này
                            }
                        }

                        return true;
                    })
                    .sorted((a, b) -> {
                        // Sắp xếp theo ngày tạo giảm dần (mới nhất trước)
                        LocalDate dateA = a.getNgayTao() != null ? a.getNgayTao() : LocalDate.MIN;
                        LocalDate dateB = b.getNgayTao() != null ? b.getNgayTao() : LocalDate.MIN;
                        return dateB.compareTo(dateA);
                    })
                    .map(this::convertToDTO)
                    .collect(Collectors.toList());

        } catch (Exception e) {
            e.printStackTrace();
            throw new RuntimeException("Lỗi khi tìm kiếm đơn hàng online: " + e.getMessage());
        }
    }

    // Tìm kiếm đơn hàng POS theo tên khách hàng và khoảng ngày
    public List<DonHangDTO> searchDonHangPOS(String tenKhachHang, String tuNgay, String denNgay, int trangThai) {
        try {
            // Lấy tất cả đơn hàng POS
            List<DonHang> allDonHangPOS = donHangRepository.findByTrangThaiAndLoaiDonHang(null, "Bán hàng tại quầy");

            // Lọc theo các điều kiện
            return allDonHangPOS.stream()
                    .filter(dh -> {
                        // Lọc theo tên khách hàng
                        if (tenKhachHang != null && !tenKhachHang.trim().isEmpty()) {
                            if (dh.getKhachHang() == null ||
                                    !dh.getKhachHang().getTenKhachHang().toLowerCase()
                                            .contains(tenKhachHang.toLowerCase().trim())) {
                                return false;
                            }
                        }

                        // Lọc theo khoảng ngày
                        if (tuNgay != null && !tuNgay.trim().isEmpty()) {
                            try {
                                LocalDate tuNgayDate = LocalDate.parse(tuNgay);
                                LocalDate ngayMuaDate = dh.getNgayMua();
                                if (ngayMuaDate == null || ngayMuaDate.isBefore(tuNgayDate)) {
                                    return false;
                                }
                            } catch (Exception e) {
                                // Nếu parse lỗi thì bỏ qua điều kiện này
                            }
                        }

                        if (denNgay != null && !denNgay.trim().isEmpty()) {
                            try {
                                LocalDate denNgayDate = LocalDate.parse(denNgay);
                                LocalDate ngayMuaDate = dh.getNgayMua();
                                if (ngayMuaDate == null || ngayMuaDate.isAfter(denNgayDate)) {
                                    return false;
                                }
                            } catch (Exception e) {
                                // Nếu parse lỗi thì bỏ qua điều kiện này
                            }
                        }

                        // Lọc theo trạng thái
                        if (trangThai >= 0 && dh.getTrangThai() != trangThai) {
                            return false;
                        }

                        return true;
                    })
                    .sorted((a, b) -> {
                        // Sắp xếp theo ngày mua giảm dần (mới nhất trước)
                        LocalDate dateA = a.getNgayMua() != null ? a.getNgayMua() : LocalDate.MIN;
                        LocalDate dateB = b.getNgayMua() != null ? b.getNgayMua() : LocalDate.MIN;
                        return dateB.compareTo(dateA);
                    })
                    .map(this::convertToDTO)
                    .collect(Collectors.toList());

        } catch (Exception e) {
            e.printStackTrace();
            throw new RuntimeException("Lỗi khi tìm kiếm đơn hàng POS: " + e.getMessage());
        }
    }

    @Transactional
    public DonHangDTO applyVoucherForClientOrder(Integer idDonHang, Integer idVoucher) {
        Optional<DonHang> optional = donHangRepository.findById(idDonHang);
        if (optional.isPresent()) {
            DonHang donHang = optional.get();

            if (idVoucher != null) {
                Voucher voucher = voucherRepository.findById(idVoucher).orElse(null);
                if (voucher == null) {
                    throw new RuntimeException("Không tìm thấy voucher");
                }

                // ✅ THÊM: Trừ số lượng voucher đi 1
                if (voucher.getSoLuong() > 0) {
                    voucher.setSoLuong(voucher.getSoLuong() - 1);
                    voucherRepository.save(voucher);  // Lưu thay đổi số lượng
                    System.out.println("✅ Đã trừ số lượng voucher: " + voucher.getMaVoucher() + " từ " + (voucher.getSoLuong() + 1) + " xuống " + voucher.getSoLuong());
                } else {
                    throw new RuntimeException("Voucher đã hết số lượng");
                }

                // Áp dụng voucher mới vào đơn hàng
                donHang.setGiamGia(voucher);
            } else {
                donHang.setGiamGia(null);
            }

            // Cập nhật tổng tiền đơn hàng
            capNhatTongTienDonHang2(idDonHang);

            return convertToDTO(donHangRepository.save(donHang));
        }
        return null;
    }

    // Thêm method mới để trừ số lượng khi thanh toán thành công
    @Transactional
    public DonHangDTO confirmPaymentAndDeductVoucher(Integer idDonHang) {
        Optional<DonHang> optional = donHangRepository.findById(idDonHang);
        if (optional.isPresent()) {
            DonHang donHang = optional.get();

            // Chỉ trừ số lượng voucher khi thanh toán thành công
            Voucher voucher = donHang.getGiamGia();
            if (voucher != null) {
                voucher.setSoLuong(voucher.getSoLuong() - 1);
                voucherRepository.save(voucher);
            }

            // Cập nhật trạng thái đơn hàng thành "Đã thanh toán"
            donHang.setTrangThai(0); // hoặc trạng thái phù hợp

            return convertToDTO(donHangRepository.save(donHang));
        }
        return null;
    }



    // OrderService.java
    public DonHangDTO createOnline(DonHangDTO dto) {
        System.out.println("📥 Dữ liệu nhận từ Frontend:");
        System.out.println(" - tongTien: " + dto.getTongTien());
        System.out.println(" - phiVanChuyen: " + dto.getPhiVanChuyen());
        System.out.println(" - tongTienGiamGia: " + dto.getTongTienGiamGia());
        System.out.println(" - idgiamGia: " + dto.getIdgiamGia());
        System.out.println(" - loaiDonHang: " + dto.getLoaiDonHang());

        DonHang donHang = convertToEntityOnline(dto);

        // ✅ LOGIC ĐƠN GIẢN: Lưu trực tiếp từ Frontend
        // Set ngày tạo
        if (donHang.getNgayTao() == null) {
            donHang.setNgayTao(LocalDate.now());
        }
        donHang.setNgayMua(null);

        // ✅ KHÔNG TÍNH TOÁN LẠI - Lưu trực tiếp từ Frontend
        // Frontend đã tính: 700k - 70k (10%) + 30k (ship) = 660k
        // Backend chỉ cần lưu 660k vào tongTien

        System.out.println("💰 Lưu trực tiếp từ Frontend:");
        System.out.println(" - tongTien: " + donHang.getTongTien());
        System.out.println(" - phiVanChuyen: " + donHang.getPhiVanChuyen());
        System.out.println(" - tongTienGiamGia: " + donHang.getTongTienGiamGia());
        System.out.println(" - ngayTao: " + donHang.getNgayTao());
        System.out.println(" - ngayMua: " + donHang.getNgayMua());

        return convertToDTOOnline(donHangRepository.save(donHang));
    }







}

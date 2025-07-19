
package com.example.backend.service;


import com.example.backend.dto.DonHangDTO;

import com.example.backend.entity.*;
import com.example.backend.repository.*;
import jakarta.transaction.Transactional;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;
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

        // Nếu có voucher thì tính giảm giá
        if (donHang.getGiamGia() != null) {
            double tongTien = donHang.getTongTien() != null ? donHang.getTongTien() : 0;
            double giam = tinhTienGiamVoucher(tongTien, donHang.getGiamGia());
            donHang.setTongTienGiamGia(giam);
            donHang.setTongTien(tongTien - giam);
        } else {
            donHang.setTongTienGiamGia(0.0);
            // donHang.setTongTien(...) // giữ nguyên tổng tiền gốc
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

    // ... existing code ...
    public DonHangDTO updateVoucher(Integer idDonHang, Integer idgiamGia) {
        Optional<DonHang> optional = donHangRepository.findById(idDonHang);
        if (optional.isPresent()) {
            DonHang donHang = optional.get();
            if (idgiamGia != null) {
                voucherService.kiemTraDieuKienVoucher(donHang, idgiamGia);
                Voucher voucher = voucherRepository.findById(idgiamGia).orElse(null);
                donHang.setGiamGia(voucher);
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
    // ... existing code ...
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

public List<DonHangDTO> filterByTrangThaiAndLoai(Integer trangThai, String loaiDonHang) {
    return donHangRepository.findByTrangThaiAndLoaiDonHang(trangThai, loaiDonHang)
            .stream()
            .map(this::convertToDTO)
            .collect(Collectors.toList());
}


    public DonHangDTO xacNhanDonHang(Integer id, Double tongTien, Integer idkhachHang, String tenKhachHang, String email, String soDienThoai) {
        Optional<DonHang> optional = donHangRepository.findById(id);
        if (optional.isPresent()) {
            DonHang donHang = optional.get();

            // Kiểm tra lại điều kiện voucher nếu có
            if (donHang.getGiamGia() != null) {
                try {
                    voucherService.kiemTraDieuKienVoucher(donHang, donHang.getGiamGia().getId());
                } catch (Exception e) {
                    // Nếu voucher không hợp lệ, reset về null và cập nhật lại tổng tiền
                    donHang.setGiamGia(null);
                    donHang.setTongTienGiamGia(0.0);
                    capNhatTongTienDonHang(donHang.getId());
                    donHangRepository.save(donHang);
                    // Báo lỗi cho FE
                    throw new RuntimeException("Voucher không đủ điều kiện, đã được reset về null!");
                }
            }

            donHang.setTrangThai(1); // Đã thanh toán
            if (tongTien != null) {
                donHang.setTongTien(tongTien);
            }
            // Xử lý khách hàng như cũ...
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
            return convertToDTO(donHangRepository.save(donHang));
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
        return dto;
    }

    private DonHang convertToEntity(DonHangDTO dto) {
        DonHang dh = new DonHang();
        dh.setNgayMua(dto.getNgayMua());
        dh.setNgayTao(dto.getNgayTao());
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
            // Kiểm tra điều kiện voucher trước khi gán!
            voucherService.kiemTraDieuKienVoucher(dh, dto.getIdgiamGia());
            Optional<Voucher> voucher = voucherRepository.findById(dto.getIdgiamGia());
            voucher.ifPresent(dh::setGiamGia);
        }

        return dh;
    }
    public void capNhatTongTienDonHang(Integer idDonHang) {
        DonHang donHang = donHangRepository.findById(idDonHang).orElseThrow();
        List<DonHangChiTiet> chiTiets = donHang.getDonHangChiTiets();
        double tongTienGoc = 0;
        for (DonHangChiTiet ct : chiTiets) {
            tongTienGoc += ct.getThanhTien();
        }

        double giam = 0.0;
        if (donHang.getGiamGia() != null) {
            giam = tinhTienGiamVoucher(tongTienGoc, donHang.getGiamGia());
        }
        donHang.setTongTienGiamGia(giam);
        donHang.setTongTien(tongTienGoc - giam);

        donHangRepository.save(donHang);
    }




}

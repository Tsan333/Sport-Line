
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
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.stream.Collectors;

import static com.example.backend.enums.TrangThaiDonHang.*;

@Service
public class DonHangService {

    @Autowired
    private DonHangChiTietRepository donHangChiTietRepository;

    @Autowired
    private SanPhamChiTietRepository spctRepo;
    @Autowired
    private DonHangRepository donHangRepository;

    @Autowired
    private NhanVienRepository nhanVienRepository;

    @Autowired
    private KhachHangRepository khachHangRepository;

    @Autowired
    private VoucherRepository voucherRepository;

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
    public void delete(Integer id) {
        Optional<DonHang> donHangOptional = donHangRepository.findById(id);
        if (donHangOptional.isPresent()) {
            DonHang donHang = donHangOptional.get();
            donHang.getDonHangChiTiets().clear(); // Xóa hết chi tiết
            donHangRepository.delete(donHang);    // Xóa đơn hàng
        }
    }

    private DonHangDTO convertToDTO(DonHang dh) {
        DonHangDTO dto = new DonHangDTO();
        dto.setId(dh.getId());
        dto.setIdkhachHang(dh.getKhachHang() != null ? dh.getKhachHang().getId() : null);
        dto.setIdnhanVien(dh.getNhanVien() != null ? dh.getNhanVien().getId() : null);
        dto.setIdgiamGia(dh.getGiamGia() != null ? dh.getGiamGia().getId() : null);
        dto.setNgayMua(dh.getNgayMua());
        dto.setNgayTao(dh.getNgayTao());
        dto.setLoaiDonHang(dh.getLoaiDonHang());
        dh.setTrangThai(dto.getTrangThai());

        dto.setTongTien(dh.getTongTien());
        dto.setTongTienGiamGia(dh.getTongTienGiamGia());
        dto.setDiaChiGiaoHang(dh.getDiaChiGiaoHang());
        dto.setEmailGiaoHang(dh.getEmailGiaoHang());
        dto.setTenNguoiNhan(dh.getTenNguoiNhan());
        dto.setSoDienThoaiGiaoHang(dh.getSoDienThoaiGiaoHang());
        return dto;
    }

    private DonHang convertToEntity(DonHangDTO dto) {
        DonHang dh = new DonHang();
        dh.setNgayMua(dto.getNgayMua());
        dh.setNgayTao(dto.getNgayTao());
        dh.setLoaiDonHang(dto.getLoaiDonHang());
        TrangThaiDonHang.fromValue(dto.getTrangThai()).name();


        dh.setTongTien(dto.getTongTien());
        dh.setTongTienGiamGia(dto.getTongTienGiamGia());
        dh.setDiaChiGiaoHang(dto.getDiaChiGiaoHang());
        dh.setEmailGiaoHang(dto.getEmailGiaoHang());
        dh.setTenNguoiNhan(dto.getTenNguoiNhan());
        dh.setSoDienThoaiGiaoHang(dto.getSoDienThoaiGiaoHang());

        if (dto.getIdnhanVien() != null) {
            Optional<NhanVien> nv = nhanVienRepository.findById(dto.getIdnhanVien());
            nv.ifPresent(dh::setNhanVien);
        }

        if (dto.getIdkhachHang() != null) {
            Optional<KhachHang> kh = khachHangRepository.findById(dto.getIdkhachHang());
            kh.ifPresent(dh::setKhachHang);
        }

        if (dto.getIdgiamGia() != null) {
            Optional<Voucher> voucher = voucherRepository.findById(dto.getIdgiamGia());
            voucher.ifPresent(dh::setGiamGia);
        }

        return dh;
    }



    // Tạo đơn mới
    public DonHangDTO taoHoaDonOnline(HoaDonOnlineRequest req) {

        KhachHang khachHang = khachHangRepository.findById(req.getIdKhachHang())
                .orElseThrow(() -> new RuntimeException("Không tìm thấy khách hàng ID: " + req.getIdKhachHang()));


        DonHang don = new DonHang();
        don.setNgayTao(LocalDate.now());
        don.setLoaiDonHang("ONLINE");
        don.setTrangThai(TrangThaiDonHang.CHO_XAC_NHAN.getValue());
        don.setDiaChiGiaoHang(req.getDiaChiGiaoHang());
        don.setSoDienThoaiGiaoHang(req.getSoDienThoaiGiaoHang());
        don.setEmailGiaoHang(req.getEmailGiaoHang());
        don.setTenNguoiNhan(req.getTenNguoiNhan());
        don.setKhachHang(khachHang);
        don = donHangRepository.save(don);


        double tongTien = 0;
        List<DonHangChiTiet> dsChiTiet = new ArrayList<>();

        for (SanPhamDatDTO dto : req.getSanPhamDat()) {
            SanPhamChiTiet sp = spctRepo.findById(dto.getIdSanPhamChiTiet())
                    .orElseThrow(() -> new RuntimeException("Không tìm thấy sản phẩm chi tiết ID: " + dto.getIdSanPhamChiTiet()));

            if (sp.getSoLuong() < dto.getSoLuong()) {
                throw new RuntimeException("Sản phẩm ID " + dto.getIdSanPhamChiTiet() + " đã hết hàng");
            }


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


        double tongTienGiamGia = 0;
        if (req.getIdVoucher() != null) {
            Voucher v = voucherRepository.findById(req.getIdVoucher())
                    .orElseThrow(() -> new RuntimeException("Không tìm thấy voucher ID: " + req.getIdVoucher()));

            double giam = "TIEN".equalsIgnoreCase(v.getLoaiVoucher())
                    ? v.getGiaTri()
                    : tongTien * v.getGiaTri() / 100.0;

            tongTienGiamGia = giam;
            don.setGiamGia(v);
            don.setTongTienGiamGia(giam);
            don.setTongTien(tongTien - giam);
        } else {
            don.setTongTien(tongTien);
            don.setTongTienGiamGia(0.0);
        }


        don.setDonHangChiTiets(dsChiTiet);
        don = donHangRepository.save(don);


        return new DonHangDTO(don);
    }




    // Xác nhận đơn
    public void xacNhanDon(Integer id) {
        DonHang d = donHangRepository.findById(id).orElseThrow();
        d.setTrangThai(XAC_NHAN.getValue());
        d.setNgayMua(LocalDate.now());
        donHangRepository.save(d);
    }


    public void huyDon(Integer idDon) {
        DonHang don = donHangRepository.findById(idDon)
                .orElseThrow(() -> new RuntimeException("Không tìm thấy đơn"));

        int trangThaiCu = don.getTrangThai();

        //  Kiểm tra trạng thái có được phép hủy
        List<Integer> trangThaiDuocHuy = List.of(0, 1, 2, 3); // Được hủy nếu chưa giao
        if (!trangThaiDuocHuy.contains(trangThaiCu)) {
            throw new RuntimeException("Không thể hủy đơn ở trạng thái: "
                    + TrangThaiDonHang.fromValue(trangThaiCu).getDisplayName());
        }

        //  Cập nhật trạng thái đơn
        don.setTrangThai(TrangThaiDonHang.DA_HUY.getValue());

        //  Hoàn lại số lượng sản phẩm
        for (DonHangChiTiet ct : don.getDonHangChiTiets()) {
            SanPhamChiTiet sp = ct.getSanPhamChiTiet();
            if (sp != null) {
                int hienTai = sp.getSoLuong();
                sp.setSoLuong(hienTai + ct.getSoLuong());
                spctRepo.save(sp);
            }
        }

        donHangRepository.save(don);


    }




    // Cập nhật địa chỉ & phí giao hàng

    public DonHangDTO capNhatDiaChiVaTinhPhi(
            Integer id,
            String diaChiMoi,
            String sdtMoi,
            String tenNguoiNhanMoi,
            String emailMoi,
            Integer districtId,
            String wardCode
    ) {
        DonHang don = donHangRepository.findById(id).orElseThrow();

        don.setDiaChiGiaoHang(diaChiMoi);
        don.setSoDienThoaiGiaoHang(sdtMoi);
        don.setTenNguoiNhan(tenNguoiNhanMoi);
        don.setEmailGiaoHang(emailMoi);
        donHangRepository.save(don);

        int phiVanChuyen = ghnClientService.tinhPhiVanChuyen(districtId, wardCode, 3000);

        DonHangDTO dto = new DonHangDTO(don);
        dto.setPhiVanChuyen(phiVanChuyen); // ✅ không lưu DB
        return dto;
    }

    private int tinhPhiGHN(int districtId, String wardCode) {
        return 30000; // giả lập
    }

    public List<DonHang> layDonTheoKhach(Integer idKhach) {
        return donHangRepository.findByKhachHangIdOrderByNgayTaoDesc(idKhach);
    }

    public DonHang layChiTietDon(Integer id) {
        DonHang don = donHangRepository.findWithChiTiet(id);
        if (don == null) throw new RuntimeException("Không tìm thấy đơn #" + id);
        return don;
    }

    public List<DonHang> layDonTheoTrangThai(Integer trangThai) {
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

    public void danhDauGiaoKhongThanhCong(Integer idDon) {
        DonHang don = donHangRepository.findById(idDon)
                .orElseThrow(() -> new RuntimeException("Không tìm thấy đơn hàng"));

        TrangThaiDonHang hienTai = TrangThaiDonHang.fromValue(don.getTrangThai());
        if (hienTai != TrangThaiDonHang.DANG_GIAO) {
            throw new RuntimeException("Chỉ có thể đánh dấu giao không thành công khi đơn đang giao");
        }

        don.setTrangThai(TrangThaiDonHang.GIAO_HANG_KHONG_THANH_CONG.getValue());
        donHangRepository.save(don);
    }

    private boolean isTrangThaiHopLe(TrangThaiDonHang hienTai, TrangThaiDonHang moi) {
        return switch (hienTai) {
            case CHO_XAC_NHAN -> moi == XAC_NHAN || moi == DA_HUY;
            case XAC_NHAN -> moi == DANG_CHUAN_BI || moi == DA_HUY;
            case DANG_CHUAN_BI -> moi == DANG_GIAO || moi == DA_HUY;
            case DANG_GIAO -> moi == DA_GIAO || moi == DA_HUY || moi == GIAO_HANG_KHONG_THANH_CONG;
            case DA_GIAO -> moi == TRA_HANG_HOAN_TIEN;
            default -> false;
        };
    }






}


package com.example.backend.service;

import com.example.backend.dto.SPCTDTO;

import com.example.backend.dto.SPCTReq;
import com.example.backend.dto.SPCTRequest;
import com.example.backend.dto.SanPhamDonHangResponse;
import com.example.backend.entity.*;

import com.example.backend.repository.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.sql.Date;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
public class SPCTService {

    @Autowired
    private SanPhamChiTietRepository spcti;


    @Autowired
    private SanPhamInterface spi;

    @Autowired
    private KichThuocInterface kti;

    @Autowired
    private MauSacInterface msi;

    @Autowired
    private KhuyenMaiRepository khuyenMaiRepository;

    @Autowired
    private KhuyenMaiService khuyenMaiService;

    public SanPhamChiTiet createSanPhamChiTiet(Integer id, SPCTRequest request) {
        request.setIdSanPham(id);

        // Kiểm tra trùng biến thể
        boolean exists = spcti.existsBySanPham_IdAndMauSac_IdAndKichThuoc_Id(
                id, request.getIdMauSac(), request.getIdKichThuoc()
        );
        if (exists) {
            throw new RuntimeException("Biến thể với màu sắc và kích thước này đã tồn tại!");
        }

        SanPham sanPham = spi.findById(request.getIdSanPham())
                .orElseThrow(() -> new RuntimeException("Không tìm thấy sản phẩm"));

        KichThuoc kichThuoc = kti.findById(request.getIdKichThuoc())
                .orElseThrow(() -> new RuntimeException("Không tìm thấy kích thước"));

        MauSac mauSac = msi.findById(request.getIdMauSac())
                .orElseThrow(() -> new RuntimeException("Không tìm thấy màu sắc"));

        SanPhamChiTiet spct = new SanPhamChiTiet();
        spct.setSanPham(sanPham);
        spct.setKichThuoc(kichThuoc);
        spct.setMauSac(mauSac);
        spct.setSoLuong(request.getSoLuong());
        spct.setGiaBan(request.getGiaBan());
        spct.setNgaySanXuat((Date) request.getNgaySanXuat());
        spct.setNgayTao(LocalDateTime.now());
        spct.setTrangThai(1); // mặc định còn bán

        return spcti.save(spct);
    }
    public SanPhamChiTiet updateSanPhamChiTiet(Integer idSpct, SPCTReq request) {
        // Tìm biến thể cũ
        SanPhamChiTiet spct = spcti.findById(idSpct)
                .orElseThrow(() -> new RuntimeException("Không tìm thấy biến thể sản phẩm"));

        // Lấy id sản phẩm, id màu sắc, id kích thước mới
        Integer idSanPham = request.getIdSanPham() != null ? request.getIdSanPham() : spct.getSanPham().getId();
        Integer idMauSac = request.getIdMauSac() != null ? request.getIdMauSac() : spct.getMauSac().getId();
        Integer idKichThuoc = request.getIdKichThuoc() != null ? request.getIdKichThuoc() : spct.getKichThuoc().getId();

        // Kiểm tra trùng biến thể (trừ chính nó)
        boolean exists = spcti.existsBySanPham_IdAndMauSac_IdAndKichThuoc_IdAndIdNot(
                idSanPham, idMauSac, idKichThuoc, idSpct
        );
        if (exists) {
            throw new RuntimeException("Biến thể này đã tồn tại!");
        }

        // Cập nhật các trường cơ bản
        if (request.getIdSanPham() != null) {
            SanPham sanPham = spi.findById(request.getIdSanPham())
                    .orElseThrow(() -> new RuntimeException("Không tìm thấy sản phẩm"));
            spct.setSanPham(sanPham);
        }
        if (request.getIdKichThuoc() != null) {
            KichThuoc kichThuoc = kti.findById(request.getIdKichThuoc())
                    .orElseThrow(() -> new RuntimeException("Không tìm thấy kích thước"));
            spct.setKichThuoc(kichThuoc);
        }
        if (request.getIdMauSac() != null) {
            MauSac mauSac = msi.findById(request.getIdMauSac())
                    .orElseThrow(() -> new RuntimeException("Không tìm thấy màu sắc"));
            spct.setMauSac(mauSac);
        }
        if (request.getSoLuong() != null) {
            spct.setSoLuong(request.getSoLuong());
        }

        // Xử lý giá bán và tự động tính lại giá khuyến mãi
        if (request.getGiaBan() != null && !request.getGiaBan().equals(spct.getGiaBan())) {
            double oldPrice = spct.getGiaBan();
            double newPrice = request.getGiaBan();

            // Cập nhật giá bán mới
            spct.setGiaBan(newPrice);

            // Nếu có khuyến mãi hiện tại, tự động tính lại giá khuyến mãi
            if (spct.getKhuyenMai() != null && spct.getGiaBanGiamGia() != null) {
                // Tính tỷ lệ giảm giá cũ
                double discountRatio = (oldPrice - spct.getGiaBanGiamGia()) / oldPrice;

                // Áp dụng tỷ lệ giảm giá cũ cho giá mới
                double newDiscountedPrice = newPrice * (1 - discountRatio);

                // Đảm bảo giá khuyến mãi không cao hơn giá bán
                if (newDiscountedPrice >= newPrice) {
                    newDiscountedPrice = newPrice;
                }

                // Cập nhật giá khuyến mãi mới
                spct.setGiaBanGiamGia((double) Math.round(newDiscountedPrice));
            }
        }

        if (request.getNgaySanXuat() != null) {
            spct.setNgaySanXuat((Date) request.getNgaySanXuat());
        }

        // Xử lý khuyến mãi mới (nếu có)
        if (request.getIdKhuyenMai() != null) {
            if (request.getIdKhuyenMai() == 0) {
                // Reset khuyến mãi về null
                spct.setKhuyenMai(null);
                spct.setGiaBanGiamGia(null);
            } else {
                try {
                    // Tìm khuyến mãi mới
                    KhuyenMai khuyenMai = khuyenMaiRepository.findById(request.getIdKhuyenMai())
                            .orElseThrow(() -> new RuntimeException("Không tìm thấy khuyến mãi"));

                    // Kiểm tra trạng thái khuyến mãi
                    if (khuyenMai.getTrangThai() != 1) {
                        throw new RuntimeException("Khuyến mãi không còn hiệu lực");
                    }

                    // Kiểm tra ngày hiệu lực
                    LocalDateTime now = LocalDateTime.now();
                    if (khuyenMai.getNgayBatDau() != null && now.isBefore(khuyenMai.getNgayBatDau())) {
                        throw new RuntimeException("Khuyến mãi chưa bắt đầu");
                    }
                    if (khuyenMai.getNgayKetThuc() != null && now.isAfter(khuyenMai.getNgayKetThuc())) {
                        throw new RuntimeException("Khuyến mãi đã hết hạn");
                    }

                    // Set khuyến mãi mới
                    spct.setKhuyenMai(khuyenMai);

                    // Tính giá khuyến mãi mới theo giá bán hiện tại
                    if (khuyenMai.getGiaTri() != null && khuyenMai.getGiaTri() > 0) {
                        double giamGia = spct.getGiaBan() * khuyenMai.getGiaTri() / 100.0;
                        double giaKhuyenMai = spct.getGiaBan() - giamGia;
                        spct.setGiaBanGiamGia((double) Math.round(giaKhuyenMai));
                    } else {
                        spct.setGiaBanGiamGia(spct.getGiaBan());
                    }

                } catch (Exception e) {
                    throw new RuntimeException("Lỗi khi xử lý khuyến mãi: " + e.getMessage());
                }
            }
        }

        return spcti.save(spct);
    }
    public List<SanPhamChiTiet> getAll() {
        return spcti.findAll();
    }


    public List<SPCTDTO> getAllForOffline() {
        return spcti.getAllSPCTDTO();
    }

    public List<SanPhamDonHangResponse> getSanPhamByDonHang(Integer idDonHang) {
        return spcti.getSanPhamByDonHang(idDonHang);
    }

    // Cập nhật method hiện tại trong SPCTService.java
    public List<SanPhamChiTiet> addSanPhamDuocKhuyenMai(Integer idKhuyenMai, List<Integer> listIdSanPham) {
        KhuyenMai khuyenMai = khuyenMaiRepository.findById(idKhuyenMai)
                .orElseThrow(() -> new IllegalArgumentException("Khuyến mãi không tồn tại"));

        List<SanPhamChiTiet> danhSachSanPham = spcti.findAllById(listIdSanPham);

        for (SanPhamChiTiet sp : danhSachSanPham) {
            // Kiểm tra xem sản phẩm đã được áp dụng khuyến mãi khác chưa
            if (sp.getKhuyenMai() != null && !sp.getKhuyenMai().getId().equals(idKhuyenMai)) {
                throw new RuntimeException("Sản phẩm " + sp.getSanPham().getTenSanPham() +
                        " đã được áp dụng khuyến mãi khác: " + sp.getKhuyenMai().getTenKhuyenMai());
            }
            sp.setKhuyenMai(khuyenMai);
        }

        khuyenMaiService.capNhatGiaKhuyenMaiChoDanhSach(danhSachSanPham);

        // Cập nhật trạng thái khuyến mãi thành 1 (đã có sản phẩm áp dụng)
        khuyenMai.setTrangThai(1);
        khuyenMaiRepository.save(khuyenMai);

        return spcti.saveAll(danhSachSanPham);
    }

    public List<SanPhamChiTiet> getSPCTDTOById(Integer id) {
        return spcti.findBySanPham_Id(id);
    }
    public SPCTDTO getSPCTDTOByIdSPCT(Integer id) {
        return spcti.getSPCTDTOById(id)
                .orElseThrow(() -> new RuntimeException("Không tìm thấy sản phẩm chi tiết"));
    }
    public List<SanPhamChiTiet> getThungrac(Integer id) {
        return spcti.findBySanPham_IdAndTrangThai(id,0);
    }
    public List<SPCTDTO> searchByTenSanPham(String keyword) {
        return spcti.searchByTenSanPham(keyword);
    }
    public List<SanPhamChiTiet> filterSPCT(Integer sanPhamId, Integer mauSacId, Integer kichThuocId, Integer trangThai) {
        return spcti.filterSPCT(sanPhamId, mauSacId, kichThuocId, trangThai);
    }

    public SanPhamChiTiet findById(Integer id) {
        return spcti.findById(id)
                .orElseThrow(() -> new RuntimeException("Không tìm thấy sản phẩm chi tiết"));
    }
    public void khoi_phuc(Integer id) {
        Optional<SanPhamChiTiet> optional = spcti.findById(id);
        if (optional.isPresent()) {
            SanPhamChiTiet spct = optional.get();
            spct.setTrangThai(1); // 1 = đang bán, 0 = đã xóa
            spcti.save(spct);
        } else {
            throw new RuntimeException("Không tìm thấy sản phẩm");
        }
    }
    public void xoa_mem(Integer id) {
        Optional<SanPhamChiTiet> optional = spcti.findById(id);
        if (optional.isPresent()) {
            SanPhamChiTiet spct = optional.get();
            spct.setTrangThai(0); // 1 = đang bán, 0 = đã xóa
            spcti.save(spct);
        } else {
            throw new RuntimeException("Không tìm thấy sản phẩm");
        }
    }
    public SanPhamChiTiet create(SanPhamChiTiet s) {
        s.setNgayTao(LocalDateTime.now());
        return spcti.save(s);
    }

    public SanPhamChiTiet update(Integer id, SanPhamChiTiet s) {
        SanPhamChiTiet old = spcti.getById(id);
        s.setId(old.getId());
        return spcti.save(s);
    }

    public void delete(Integer id) {
        spcti.deleteById(id);
    }

    // Thêm vào SPCTService.java
    public List<SanPhamChiTiet> getAvailableProductsForPromotion(Integer khuyenMaiId) {
        // Lấy sản phẩm chưa được áp dụng khuyến mãi nào (khuyenMai = null)
        // HOẶC đang áp dụng khuyến mãi này (khuyenMai.id = khuyenMaiId)
        return spcti.findByKhuyenMaiIsNullOrKhuyenMaiId(khuyenMaiId);
    }

    @Transactional
    public void removePromotionFromProducts(Integer khuyenMaiId, List<Integer> productIds) {
        // Bỏ khuyến mãi khỏi sản phẩm
        for (Integer productId : productIds) {
            SanPhamChiTiet spct = spcti.findById(productId)
                    .orElseThrow(() -> new RuntimeException("Không tìm thấy sản phẩm"));

            if (spct.getKhuyenMai() != null && spct.getKhuyenMai().getId().equals(khuyenMaiId)) {
                spct.setKhuyenMai(null);
                spct.setGiaBanGiamGia(spct.getGiaBan()); // Reset giá về giá gốc
                spcti.save(spct);
            }
        }

        // Kiểm tra xem khuyến mãi còn sản phẩm nào áp dụng không
        long count = spcti.countByKhuyenMaiId(khuyenMaiId);
        if (count == 0) {
            // Nếu không còn sản phẩm nào áp dụng, set trạng thái về 0
            KhuyenMai khuyenMai = khuyenMaiRepository.findById(khuyenMaiId)
                    .orElseThrow(() -> new RuntimeException("Không tìm thấy khuyến mãi"));
            khuyenMai.setTrangThai(0);
            khuyenMaiRepository.save(khuyenMai);
        }
    }








}

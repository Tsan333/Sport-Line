
package com.example.backend.service;

import com.example.backend.dto.SPCTDTO;

import com.example.backend.dto.SPCTRequest;
import com.example.backend.entity.KichThuoc;
import com.example.backend.entity.MauSac;
import com.example.backend.entity.SanPham;
import com.example.backend.entity.SanPhamChiTiet;

import com.example.backend.repository.KichThuocInterface;
import com.example.backend.repository.MauSacInterface;
import com.example.backend.repository.SanPhamChiTietRepository;
import com.example.backend.repository.SanPhamInterface;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.sql.Date;
import java.time.LocalDateTime;
import java.util.List;

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

    public SanPhamChiTiet createSanPhamChiTiet(Integer id,SPCTRequest request) {
        request.setIdSanPham(id);
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

    public List<SanPhamChiTiet> getAll() {
        return spcti.findAll();
    }


    public List<SPCTDTO> getAllForOffline() {
        return spcti.getAllSPCTDTO();
    }

    public SPCTDTO getSPCTDTOById(Integer id) {
        return spcti.getSPCTDTOById(id)
                .orElseThrow(() -> new RuntimeException("Không tìm thấy sản phẩm chi tiết"));
    }

    public List<SPCTDTO> searchByTenSanPham(String keyword) {
        return spcti.searchByTenSanPham(keyword);
    }


    public SanPhamChiTiet findById(Integer id) {
        return spcti.findById(id)
                .orElseThrow(() -> new RuntimeException("Không tìm thấy sản phẩm chi tiết"));
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
}

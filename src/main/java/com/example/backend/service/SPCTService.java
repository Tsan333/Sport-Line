
package com.example.backend.service;

import com.example.backend.dto.SPCTDTO;

import com.example.backend.entity.SanPhamChiTiet;

import com.example.backend.repository.SanPhamChiTietRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

@Service
public class SPCTService {

    @Autowired
    private SanPhamChiTietRepository spcti;

    public List<SanPhamChiTiet> getAll() {
        return spcti.findAll();
    }


    public List<SPCTDTO> getAllForOffline() {
        return spcti.getAllSPCTDTO();
    }

    public List<SanPhamChiTiet> getSPCTDTOById(Integer id) {
        return spcti.findBySanPham_Id(id);
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

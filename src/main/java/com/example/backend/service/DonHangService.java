package com.example.backend.service;


import com.example.backend.dto.DonHangDTO;
import com.example.backend.entity.DonHang;
import com.example.backend.entity.KhachHang;
import com.example.backend.entity.NhanVien;
import com.example.backend.entity.Voucher;
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
    private VoucherRepository voucherRepository;

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
            Optional<Voucher> voucher = voucherRepository.findById(dto.getIdgiamGia());
            voucher.ifPresent(dh::setGiamGia);
        }

        return dh;
    }
}

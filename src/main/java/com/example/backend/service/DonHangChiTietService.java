package com.example.backend.service;



import com.example.backend.entity.DonHangChiTiet;
import com.example.backend.dto.DonHangChiTietDTO;
import com.example.backend.repository.DonHangChiTietRepository;
import com.example.backend.repository.DonHangRepository;
import com.example.backend.repository.SanPhamChiTietRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;


import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
public class DonHangChiTietService {


    @Autowired
    private DonHangChiTietRepository chiTietRepository;

    @Autowired
    private DonHangRepository donHangRepository;

    @Autowired
    private SanPhamChiTietRepository sanPhamChiTietRepository;

    public List<DonHangChiTietDTO> getAll() {
        return chiTietRepository.findAll()
                .stream()
                .map(this::convertToDTO)
                .collect(Collectors.toList());
    }

    public DonHangChiTietDTO getById(int id) {
        return chiTietRepository.findById(id)
                .map(this::convertToDTO)
                .orElse(null);
    }

    public DonHangChiTietDTO create(DonHangChiTietDTO dto) {
        DonHangChiTiet chiTiet = convertToEntity(dto);
        return convertToDTO(chiTietRepository.save(chiTiet));
    }

    public DonHangChiTietDTO update(int id, DonHangChiTietDTO dto) {
        Optional<DonHangChiTiet> optional = chiTietRepository.findById(id);
        if (optional.isPresent()) {
            DonHangChiTiet chiTiet = convertToEntity(dto);
            chiTiet.setId(id);
            return convertToDTO(chiTietRepository.save(chiTiet));
        }
        return null;
    }

    public void delete(int id) {
        chiTietRepository.deleteById(id);
    }

    private DonHangChiTietDTO convertToDTO(DonHangChiTiet ct) {
        DonHangChiTietDTO dto = new DonHangChiTietDTO();
        dto.setId(ct.getId());
        dto.setIdDonHang(ct.getDonHang() != null ? ct.getDonHang().getId() : null);
        dto.setIdSanPhamChiTiet(ct.getSanPhamChiTiet() != null ? ct.getSanPhamChiTiet().getId() : null);
        dto.setSoLuong(ct.getSoLuong());
        dto.setGia(ct.getGia());
        dto.setThanhTien(ct.getThanhTien());
        return dto;
    }

    private DonHangChiTiet convertToEntity(DonHangChiTietDTO dto) {
        DonHangChiTiet ct = new DonHangChiTiet();
        ct.setSoLuong(dto.getSoLuong());
        ct.setGia(dto.getGia());
        ct.setThanhTien(dto.getThanhTien());

        if (dto.getIdDonHang() != null) {
            donHangRepository.findById(dto.getIdDonHang()).ifPresent(ct::setDonHang);
        }

        if (dto.getIdSanPhamChiTiet() != null) {
            sanPhamChiTietRepository.findById(dto.getIdSanPhamChiTiet()).ifPresent(ct::setSanPhamChiTiet);
        }

        return ct;
    }
}

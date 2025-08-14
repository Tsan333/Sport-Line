
package com.example.backend.service;


import com.example.backend.dto.DangKyRequest;
import com.example.backend.dto.KhachHangReponseDTO;

import com.example.backend.entity.KhachHang;
import com.example.backend.repository.KhachHangRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import com.example.backend.dto.PageReSponse;
import org.springframework.security.crypto.password.PasswordEncoder;

import java.util.List;
import java.util.Optional;


@Service
public class KhachHangService {

    @Autowired
    private KhachHangRepository khachHangRepository;

    private KhachHangReponseDTO convertDTO(KhachHang kh){
        return   new KhachHangReponseDTO(
                kh.getId(),
                kh.getTenKhachHang(),
                kh.getEmail(),
                kh.getNgaySinh(),
                kh.getGioiTinh(),
                kh.getDiaChi(),
                kh.getSoDienThoai(),

                kh.getTrangThai(),
                kh.getMaThongBao(),
                kh       .getThoiGianThongBao()
        );

    }

    public List<KhachHangReponseDTO> findAll(){
        return khachHangRepository.findAll().stream()
                .map(khachHang -> new KhachHangReponseDTO(
                        khachHang.getId(),
                        khachHang.getTenKhachHang(),
                        khachHang.getEmail(),
                        khachHang.getNgaySinh(),
                        khachHang.getGioiTinh(),
                        khachHang.getDiaChi(),
                        khachHang.getSoDienThoai(),

                        khachHang.getTrangThai(),
                        khachHang.getMaThongBao(),
                        khachHang.getThoiGianThongBao()
                ))
                .toList()
                ;


    }

    public KhachHangReponseDTO findAllbyid(int id) {
        return khachHangRepository.findById(id)
                .map(khachHang -> new KhachHangReponseDTO(
                        khachHang.getId(),
                        khachHang.getTenKhachHang(),
                        khachHang.getEmail(),
                        khachHang.getNgaySinh(),
                        khachHang.getGioiTinh(),
                        khachHang.getDiaChi(),
                        khachHang.getSoDienThoai(),

                        khachHang.getTrangThai(),
                        khachHang.getMaThongBao(),
                        khachHang.getThoiGianThongBao()
                ))
                .orElse(null);

    }
    // ham them
    // KhachHangService.java
    public KhachHangReponseDTO create(KhachHangReponseDTO dto) {
        // Kiểm tra trùng số điện thoại
        Optional<KhachHang> existing = khachHangRepository.findBySoDienThoai(dto.getSoDienThoai());
        if (existing.isPresent()) {
            throw new RuntimeException("Số điện thoại đã tồn tại!");
        }

        KhachHang kh = new KhachHang();
        kh.setTenKhachHang(dto.getTenKhachHang());
        kh.setEmail(dto.getEmail());
        kh.setNgaySinh(dto.getNgaySinh());
        kh.setGioiTinh(dto.getGioiTinh());
        kh.setDiaChi(dto.getDiaChi());
        kh.setSoDienThoai(dto.getSoDienThoai());
        kh.setTrangThai(dto.getTrangThai());
        kh.setMaThongBao(dto.getMaThongBao());
        kh.setThoiGianThongBao(dto.getThoiGianThongBao());
        return convertDTO(khachHangRepository.save(kh));
    }

    public Boolean deleteById(int id) {
        if (khachHangRepository.existsById(id)) {
            khachHangRepository.deleteById(id);
            return true;
        }
        return      false;
    }

    @Autowired
    private PasswordEncoder encoder;



    public void dangKyKhach(DangKyRequest req) {
        // Validate mật khẩu xác nhận
        if (req.getMatKhau() == null || req.getMatKhau().trim().isEmpty()) {
            throw new RuntimeException("Mật khẩu không được để trống!");
        }

        if (req.getConfirmPassword() == null || req.getConfirmPassword().trim().isEmpty()) {
            throw new RuntimeException("Xác nhận mật khẩu không được để trống!");
        }

        if (!req.getMatKhau().equals(req.getConfirmPassword())) {
            throw new RuntimeException("Mật khẩu xác nhận không khớp!");
        }

        // Validate độ dài mật khẩu
        if (req.getMatKhau().length() < 6) {
            throw new RuntimeException("Mật khẩu phải có ít nhất 6 ký tự!");
        }

        if (req.getMatKhau().length() > 20) {
            throw new RuntimeException("Mật khẩu không được quá 20 ký tự!");
        }

        // Validate email
        if (req.getEmail() == null || req.getEmail().trim().isEmpty()) {
            throw new RuntimeException("Email không được để trống!");
        }

        if (!req.getEmail().matches("^[A-Za-z0-9+_.-]+@(.+)$")) {
            throw new RuntimeException("Email không hợp lệ!");
        }

        if (req.getEmail().length() > 100) {
            throw new RuntimeException("Email không được quá 100 ký tự!");
        }

        // Validate họ tên
        if (req.getTenKhachHang() == null || req.getTenKhachHang().trim().isEmpty()) {
            throw new RuntimeException("Họ tên không được để trống!");
        }

        if (req.getTenKhachHang().length() < 2) {
            throw new RuntimeException("Họ tên phải có ít nhất 2 ký tự!");
        }

        if (req.getTenKhachHang().length() > 100) {
            throw new RuntimeException("Họ tên không được quá 100 ký tự!");
        }

        // Validate số điện thoại
        if (req.getSoDienThoai() == null || req.getSoDienThoai().trim().isEmpty()) {
            throw new RuntimeException("Số điện thoại không được để trống!");
        }

        if (!req.getSoDienThoai().matches("^[0-9]{10,11}$")) {
            throw new RuntimeException("Số điện thoại phải có 10-11 chữ số!");
        }

        // Validate giới tính
        if (req.getGioiTinh() == null) {
            throw new RuntimeException("Giới tính không được để trống!");
        }

        // Validate địa chỉ
        if (req.getDiaChi() != null && req.getDiaChi().length() > 300) {
            throw new RuntimeException("Địa chỉ không được quá 300 ký tự!");
        }

        // Kiểm tra email đã tồn tại
        if (khachHangRepository.existsByEmail(req.getEmail())) {
            throw new RuntimeException("Email đã được sử dụng!");
        }

        // Tạo đối tượng KhachHang
        KhachHang kh = new KhachHang();
        kh.setEmail(req.getEmail().trim());
        kh.setMatKhau(encoder.encode(req.getMatKhau()));
        kh.setTenKhachHang(req.getTenKhachHang().trim());
        kh.setSoDienThoai(req.getSoDienThoai().trim());
        kh.setTrangThai(true);
        kh.setGioiTinh(req.getGioiTinh());
        kh.setDiaChi(req.getDiaChi() != null ? req.getDiaChi().trim() : "");


        khachHangRepository.save(kh);
    }
    //phan trang khach hang
    public PageReSponse<KhachHangReponseDTO> getPaged(int page, int size) {
        Pageable pageable = PageRequest.of(page, size);
        Page<KhachHang> pageResult = khachHangRepository.findAll(pageable);

        List<KhachHangReponseDTO> content = pageResult.getContent().stream()
                .map(this::convertDTO) // convert từ Entity sang DTO
                .toList();

        PageReSponse<KhachHangReponseDTO> response = new PageReSponse<>();
        response.setContent(content);
        response.setPageNumber(pageResult.getNumber());
        response.setPageSize(pageResult.getSize());
        response.setTotalElements(pageResult.getTotalElements());
        response.setTotalPages(pageResult.getTotalPages());
        response.setLast(pageResult.isLast());

        return response;
    }
    //tim kiem khach hang theo ten, sdt, email
    public List<KhachHangReponseDTO> search(String keyword) {
        List<KhachHang> result = khachHangRepository.search(keyword);
        return result.stream()
                .map(this::convertDTO)
                .toList();
    }
    public KhachHangReponseDTO update( int id,KhachHangReponseDTO dto) {
        return khachHangRepository.findById(id)
                .map(kh -> {
                    kh.setTenKhachHang(dto.getTenKhachHang());
                    kh.setEmail(dto.getEmail());
                    kh.setNgaySinh(dto.getNgaySinh());
                    kh.setGioiTinh(dto.getGioiTinh());
                    kh.setDiaChi(dto.getDiaChi());
                    kh.setSoDienThoai(dto.getSoDienThoai());

                    kh.setTrangThai(dto.getTrangThai());
                    kh.setMaThongBao(dto.getMaThongBao());
                    kh.setThoiGianThongBao(dto.getThoiGianThongBao());
                    return convertDTO(khachHangRepository.save(kh));
                })
                .orElse(null);

    }
}

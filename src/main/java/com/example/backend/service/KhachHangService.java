
package com.example.backend.service;


import com.example.backend.dto.DangKyRequest;
import com.example.backend.dto.KhachHangReponseDTO;

import com.example.backend.entity.KhachHang;
import com.example.backend.repository.KhachHangRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.sql.Date;
import java.util.List;



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
    public KhachHangReponseDTO create(KhachHangReponseDTO dto){
        KhachHang kh = new KhachHang();
        kh.setTenKhachHang(dto.getTenKhachHang());
        kh.setEmail(dto.getEmail());
        kh.setNgaySinh((Date) dto.getNgaySinh());
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

    public KhachHangReponseDTO update( int id,KhachHangReponseDTO dto) {
        return khachHangRepository.findById(id)
                .map(kh -> {
                    kh.setTenKhachHang(dto.getTenKhachHang());
                    kh.setEmail(dto.getEmail());
                    kh.setNgaySinh((Date) dto.getNgaySinh());
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

    @Autowired
    private PasswordEncoder encoder;



    public void dangKyKhach(DangKyRequest req) {
        if (!req.getMatKhau().equals(req.getConfirmPassword())) {
            throw new RuntimeException("Mật khẩu xác nhận không khớp!");
        }

        if (khachHangRepository.existsByEmail(req.getEmail())) {
            throw new RuntimeException("Email đã được sử dụng!");
        }

        KhachHang kh = new KhachHang();
        kh.setEmail(req.getEmail());
        kh.setMatKhau(encoder.encode(req.getMatKhau()));
        kh.setTenKhachHang(req.getTenKhachHang());
        kh.setSoDienThoai(req.getSoDienThoai());
        kh.setTrangThai(true);
        kh.setGioiTinh(true);
        kh.setDiaChi(req.getDiaChi());


        khachHangRepository.save(kh);
    }
}


package com.example.backend.service;


import com.example.backend.dto.KhuyenMaiDTO;
import com.example.backend.entity.KhuyenMai;

import com.example.backend.repository.KhuyenMaiRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.sql.Date;
import java.util.List;

@Service
public class KhuyenMaiService {

    @Autowired
    private KhuyenMaiRepository khuyenMaiRepository;

    public KhuyenMaiDTO convertDTO(KhuyenMai km){
        return new KhuyenMaiDTO(
          km.getId(),
          km.getTenKhuyenMai(),
          km.getLoaiKhuyenMai(),
          km.getMoTa(),
          km.getGiaTri(),
          km.getDonToiThieu(),
          km.getNgayBatDau(),
          km.getNgayKetThuc(),
          km.getTrangThai()
        );
    }
    // ham lay all khuyen mai
    public List<KhuyenMaiDTO> getall(){
        return khuyenMaiRepository.findAll().stream()
                .map(khuyenMai -> new KhuyenMaiDTO(
                        khuyenMai.getId(),
                        khuyenMai.getTenKhuyenMai(),
                        khuyenMai.getLoaiKhuyenMai(),
                        khuyenMai.getMoTa(),
                        khuyenMai.getGiaTri(),
                        khuyenMai.getDonToiThieu(),
                        khuyenMai.getNgayBatDau(),
                        khuyenMai.getNgayKetThuc(),
                        khuyenMai.getTrangThai()
                )).toList();
    }
    //ham lay danh sach theo id
    public KhuyenMaiDTO findById(Integer id){
        return khuyenMaiRepository.findById(id)
                .map(khuyenMai -> new KhuyenMaiDTO(
                        khuyenMai.getId(),
                        khuyenMai.getTenKhuyenMai(),
                        khuyenMai.getLoaiKhuyenMai(),
                        khuyenMai.getMoTa(),
                        khuyenMai.getGiaTri(),
                        khuyenMai.getDonToiThieu(),
                        khuyenMai.getNgayBatDau(),
                        khuyenMai.getNgayKetThuc(),
                        khuyenMai.getTrangThai()
                ))
                .orElse(null);
    }

    // ham create khuyenmai
    public KhuyenMaiDTO create(KhuyenMaiDTO dto){
        KhuyenMai km = new KhuyenMai();
        km.setTenKhuyenMai(dto.getTenKhuyenMai());
        km.setLoaiKhuyenMai(dto.getLoaiKhuyenMai());
        km.setMoTa(dto.getMoTa());
        km.setGiaTri(dto.getGiaTri());
        km.setDonToiThieu(dto.getDonToiThieu());
        km.setNgayBatDau((Date) dto.getNgayBatDau());
        km.setNgayKetThuc((Date) dto.getNgayKetThuc());
        km.setTrangThai(dto.getTrangThai());

        return convertDTO(khuyenMaiRepository.save(km));

    }

    // ham delete khuyen mai

    public boolean delete(Integer id){
        if (khuyenMaiRepository.existsById(id)) {
            khuyenMaiRepository.deleteById(id);
            return true;
        }
        return false;
    }

    //ham update khuyen mai
    public KhuyenMaiDTO update (int id, KhuyenMaiDTO dto){
        return khuyenMaiRepository.findById(id)
                .map( km -> {
                    km.setTenKhuyenMai(dto.getTenKhuyenMai());
                    km.setLoaiKhuyenMai(dto.getLoaiKhuyenMai());
                    km.setMoTa(dto.getMoTa());
                    km.setGiaTri(dto.getGiaTri());
                    km.setDonToiThieu(dto.getDonToiThieu());
                    km.setNgayBatDau((Date) dto.getNgayBatDau());
                    km.setNgayKetThuc((Date) dto.getNgayKetThuc());
                    km.setTrangThai(dto.getTrangThai());

                    return convertDTO(khuyenMaiRepository.save(km));
                })
                .orElse(null);
    }
}

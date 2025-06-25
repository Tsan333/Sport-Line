package com.example.backend.service;



import com.example.backend.dto.VoucherDTO;
import com.example.backend.entity.Voucher;
import com.example.backend.repository.VoucherRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class VoucherService {

    @Autowired
    private VoucherRepository voucherRepository;

    public VoucherDTO convertDTO(Voucher voucher){
        return new VoucherDTO(
                voucher.getId(),
                voucher.getMaVoucher(),
                voucher.getTenVoucher(),
                voucher.getLoaiVoucher(),
                voucher.getMoTa(),
                voucher.getSoLuong(),
                voucher.getGiaTri(),
                voucher.getDonToiThieu(),
                voucher.getNgayBatDau(),
                voucher.getNgayKetThuc(),
                voucher.getNgayTao(),
                voucher.getTrangThai()

        );
    }

    // ham lay all voucher
    public List<VoucherDTO> getall(){
        return voucherRepository.findAll().stream()
                .map(voucher -> new VoucherDTO(
                        voucher.getId(),
                        voucher.getMaVoucher(),
                        voucher.getTenVoucher(),
                        voucher.getLoaiVoucher(),
                        voucher.getMoTa(),
                        voucher.getSoLuong(),
                        voucher.getGiaTri(),
                        voucher.getDonToiThieu(),
                        voucher.getNgayBatDau(),
                        voucher.getNgayKetThuc(),
                        voucher.getNgayTao(),
                        voucher.getTrangThai()
                )).toList();
    }

    //ham lay danh sach theo id
    public VoucherDTO findById(Integer id){
        return voucherRepository.findById(id)
                .map(voucher -> new VoucherDTO(
                        voucher.getId(),
                        voucher.getMaVoucher(),
                        voucher.getTenVoucher(),
                        voucher.getLoaiVoucher(),
                        voucher.getMoTa(),
                        voucher.getSoLuong(),
                        voucher.getGiaTri(),
                        voucher.getDonToiThieu(),
                        voucher.getNgayBatDau(),
                        voucher.getNgayKetThuc(),
                        voucher.getNgayTao(),
                        voucher.getTrangThai()
                ))
                .orElse(null);
    }

    // ham create voucher
    public VoucherDTO create(VoucherDTO dto){
        Voucher v = new Voucher();
        v.setMaVoucher(dto.getMaVoucher());
        v.setTenVoucher(dto.getTenVoucher());
        v.setLoaiVoucher(dto.getLoaiVoucher());
        v.setSoLuong(dto.getSoLuong());
        v.setMoTa(dto.getMoTa());
        v.setGiaTri(dto.getGiaTri());
        v.setDonToiThieu(dto.getDonToiThieu());
        v.setNgayBatDau(dto.getNgayBatDau());
        v.setNgayKetThuc(dto.getNgayKetThuc());
        v.setNgayTao(dto.getNgayTao());
        v.setTrangThai(dto.getTrangThai());

        return convertDTO(voucherRepository.save(v));

    }

    // ham delete vouccher
    public boolean delete(Integer id){
        if (voucherRepository.existsById(id)) {
            voucherRepository.deleteById(id);
            return true;
        }
        return false;
    }

    //ham update voucher
    public VoucherDTO update (int id, VoucherDTO dto){
        return voucherRepository.findById(id)
                .map( v  -> {
                    v.setMaVoucher(dto.getMaVoucher());
                    v.setTenVoucher(dto.getTenVoucher());
                    v.setLoaiVoucher(dto.getLoaiVoucher());
                    v.setSoLuong(dto.getSoLuong());
                    v.setMoTa(dto.getMoTa());
                    v.setGiaTri(dto.getGiaTri());
                    v.setDonToiThieu(dto.getDonToiThieu());
                    v.setNgayBatDau(dto.getNgayBatDau());
                    v.setNgayKetThuc(dto.getNgayKetThuc());
                    v.setNgayTao(dto.getNgayTao());
                    v.setTrangThai(dto.getTrangThai());

                    return convertDTO(voucherRepository.save(v));
                })
                .orElse(null);
    }

}

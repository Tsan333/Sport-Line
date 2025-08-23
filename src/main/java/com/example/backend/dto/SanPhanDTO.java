package com.example.backend.dto;




import com.example.backend.entity.ChatLieu;
import com.example.backend.entity.DanhMuc;
import com.example.backend.entity.ThuongHieu;
import com.example.backend.entity.XuatXu;
import lombok.Data;

@Data
public class SanPhanDTO {
    private int id;
    private String tenSanPham;
    private Double giaBan;           // Giá gốc (để gạch đi)
    private Double giaBanGiamGia;    // Giá sau giảm (giá cuối)
    private Double phanTramGiam;     // Phần trăm giảm giá
    private DanhMuc danhMuc;
    private ThuongHieu thuongHieu;
    private ChatLieu chatLieu;
    private XuatXu xuatXu;
    private String imanges;
    private Integer trangThai;

    public SanPhanDTO(Integer id, String tenSanPham,
                      Double giaBan, Double giaBanGiamGia, Double phanTramGiam,
                      DanhMuc danhMuc, ThuongHieu thuongHieu,
                      ChatLieu chatLieu, XuatXu xuatXu,
                      String imanges, Integer trangThai) {
        this.id = id;
        this.tenSanPham = tenSanPham;
        this.giaBan = giaBan;
        this.giaBanGiamGia = giaBanGiamGia;
        this.phanTramGiam = phanTramGiam;
        this.danhMuc = danhMuc;
        this.thuongHieu = thuongHieu;
        this.chatLieu = chatLieu;
        this.xuatXu = xuatXu;
        this.imanges = imanges;
        this.trangThai = trangThai;
    }
}
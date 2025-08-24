package com.example.backend.service;



import com.example.backend.entity.DonHang;
import com.example.backend.entity.DonHangChiTiet;
import com.example.backend.dto.DonHangChiTietDTO;
import com.example.backend.entity.SanPhamChiTiet;
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
    private DonHangService donHangService;

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

    //    public DonHangChiTietDTO create(DonHangChiTietDTO dto) {
//        DonHangChiTiet chiTiet = convertToEntity(dto);
//        return convertToDTO(chiTietRepository.save(chiTiet));
//    }
    public List<DonHangChiTietDTO> getDonHangById(Integer id) {
        return chiTietRepository.findByDonHangId(id);
    }
    public DonHangChiTietDTO create(DonHangChiTietDTO dto) {
        // 1. Lấy sản phẩm chi tiết từ DB
        SanPhamChiTiet spct = sanPhamChiTietRepository.findById(dto.getIdSanPhamChiTiet())
                .orElseThrow(() -> new RuntimeException("Không tìm thấy sản phẩm chi tiết!"));

        // 2. Kiểm tra tồn kho
        if (spct.getSoLuong() < dto.getSoLuong()) {
            throw new RuntimeException("Số lượng tồn kho không đủ!");
        }

        // 3. Trừ tồn kho
        spct.setSoLuong(spct.getSoLuong() - dto.getSoLuong());
        sanPhamChiTietRepository.save(spct);

        // 4. Xử lý cộng dồn hoặc tạo mới chi tiết hóa đơn
        Optional<DonHangChiTiet> optional = chiTietRepository
                .findByDonHang_IdAndSanPhamChiTiet_Id(dto.getIdDonHang(), dto.getIdSanPhamChiTiet());

        DonHangChiTiet chiTiet;
        if (optional.isPresent()) {
            chiTiet = optional.get();
            chiTiet.setSoLuong(chiTiet.getSoLuong() + dto.getSoLuong());
            chiTiet.setThanhTien(chiTiet.getThanhTien() + dto.getThanhTien());
        } else {
            chiTiet = convertToEntity(dto);
        }

        // 5. Lưu và cập nhật tổng tiền đơn hàng
        DonHangChiTiet saved = chiTietRepository.save(chiTiet);
        donHangService.capNhatTongTienDonHang(dto.getIdDonHang());

        return convertToDTO(saved);
    }
    public DonHangChiTietDTO create_k_tru_sl(DonHangChiTietDTO dto) {
        // 1. Lấy sản phẩm chi tiết từ DB
        SanPhamChiTiet spct = sanPhamChiTietRepository.findById(dto.getIdSanPhamChiTiet())
                .orElseThrow(() -> new RuntimeException("Không tìm thấy sản phẩm chi tiết!"));

        // ✅ GIỮ: Kiểm tra tồn kho (để đảm bảo có đủ hàng)
        if (spct.getSoLuong() < dto.getSoLuong()) {
            throw new RuntimeException("Số lượng tồn kho không đủ!");
        }

        // ❌ BỎ: Chỉ bỏ phần trừ tồn kho
        // spct.setSoLuong(spct.getSoLuong() - dto.getSoLuong());
        // sanPhamChiTietRepository.save(spct);

        // 2. Xử lý cộng dồn hoặc tạo mới chi tiết hóa đơn
        Optional<DonHangChiTiet> optional = chiTietRepository
                .findByDonHang_IdAndSanPhamChiTiet_Id(dto.getIdDonHang(), dto.getIdSanPhamChiTiet());

        DonHangChiTiet chiTiet;
        if (optional.isPresent()) {
            chiTiet = optional.get();
            chiTiet.setSoLuong(chiTiet.getSoLuong() + dto.getSoLuong());
            chiTiet.setThanhTien(chiTiet.getThanhTien() + dto.getThanhTien());
        } else {
            chiTiet = convertToEntity(dto);
        }

        // 3. Lưu và cập nhật tổng tiền đơn hàng
        DonHangChiTiet saved = chiTietRepository.save(chiTiet);
        donHangService.capNhatTongTienDonHang(dto.getIdDonHang());

        return convertToDTO(saved);
    }

//    public DonHangChiTietDTO themSPvaoDH(DonHangChiTietDTO dto) {
//        // 1. Lấy sản phẩm chi tiết từ DB
//        SanPhamChiTiet spct = sanPhamChiTietRepository.findById(dto.getIdSanPhamChiTiet())
//                .orElseThrow(() -> new RuntimeException("Không tìm thấy sản phẩm chi tiết!"));
//
//
//
//        // ✅ GIỮ NGUYÊN: Xử lý cộng dồn hoặc tạo mới chi tiết hóa đơn
//        Optional<DonHangChiTiet> optional = chiTietRepository
//                .findByDonHang_IdAndSanPhamChiTiet_Id(dto.getIdDonHang(), dto.getIdSanPhamChiTiet());
//
//        DonHangChiTiet chiTiet;
//        if (optional.isPresent()) {
//            chiTiet = optional.get();
//            chiTiet.setSoLuong(chiTiet.getSoLuong() + dto.getSoLuong());
//            chiTiet.setThanhTien(chiTiet.getThanhTien() + dto.getThanhTien());
//        } else {
//            chiTiet = convertToEntity(dto);
//        }
//
//        // 5. Lưu và cập nhật tổng tiền đơn hàng
//        DonHangChiTiet saved = chiTietRepository.save(chiTiet);
//        donHangService.capNhatTongTienDonHang2(dto.getIdDonHang());
//
//        return convertToDTO(saved);
//    }
public DonHangChiTietDTO themSPvaoDH(DonHangChiTietDTO dto) {
    // 1. Lấy sản phẩm chi tiết từ DB
    SanPhamChiTiet spct = sanPhamChiTietRepository.findById(dto.getIdSanPhamChiTiet())
            .orElseThrow(() -> new RuntimeException("Không tìm thấy sản phẩm chi tiết!"));

    // 2. Lấy thông tin đơn hàng để kiểm tra trạng thái
    DonHang donHang = donHangRepository.findById(dto.getIdDonHang())
            .orElseThrow(() -> new RuntimeException("Không tìm thấy đơn hàng!"));

    // 3. ✅ THÊM: Kiểm tra tồn kho cho trạng thái 0 (chờ xác nhận)
    if (donHang.getTrangThai() == 0) {
        // Trạng thái 0: Kiểm tra tồn kho dựa trên tổng số lượng sẽ đặt
        int soLuongDaDat = 0;

        // Tìm sản phẩm đã có trong đơn hàng này
        Optional<DonHangChiTiet> existingItem = chiTietRepository
                .findByDonHang_IdAndSanPhamChiTiet_Id(dto.getIdDonHang(), dto.getIdSanPhamChiTiet());

        if (existingItem.isPresent()) {
            soLuongDaDat = existingItem.get().getSoLuong();
        }

        // Tổng số lượng sau khi thêm = Số lượng đã có + Số lượng mới
        int tongSoLuongSeDat = soLuongDaDat + dto.getSoLuong();

        // Kiểm tra: Tồn kho hiện tại >= Tổng số lượng sẽ đặt
        if (spct.getSoLuong() < tongSoLuongSeDat) {
            throw new RuntimeException("Không đủ tồn kho! Tồn kho: " + spct.getSoLuong() +
                    ", Đã đặt: " + soLuongDaDat +
                    ", Thêm mới: " + dto.getSoLuong() +
                    ", Tổng cần: " + tongSoLuongSeDat);
        }
    }

    // ✅ GIỮ NGUYÊN: Xử lý cộng dồn hoặc tạo mới chi tiết hóa đơn
    Optional<DonHangChiTiet> optional = chiTietRepository
            .findByDonHang_IdAndSanPhamChiTiet_Id(dto.getIdDonHang(), dto.getIdSanPhamChiTiet());

    DonHangChiTiet chiTiet;
    if (optional.isPresent()) {
        chiTiet = optional.get();
        chiTiet.setSoLuong(chiTiet.getSoLuong() + dto.getSoLuong());
        chiTiet.setThanhTien(chiTiet.getThanhTien() + dto.getThanhTien());
    } else {
        chiTiet = convertToEntity(dto);
    }

    // 5. Lưu và cập nhật tổng tiền đơn hàng
    DonHangChiTiet saved = chiTietRepository.save(chiTiet);
    donHangService.capNhatTongTienDonHang2(dto.getIdDonHang());

    return convertToDTO(saved);
}


    public DonHangChiTietDTO update(int id, DonHangChiTietDTO dto) {
        Optional<DonHangChiTiet> optional = chiTietRepository.findById(id);
        if (optional.isPresent()) {
            DonHangChiTiet chiTiet = optional.get();
            int oldQty = chiTiet.getSoLuong();
            int newQty = dto.getSoLuong();
            int diff = newQty - oldQty;

            SanPhamChiTiet spct = chiTiet.getSanPhamChiTiet();
            if (diff > 0) {
                if (spct.getSoLuong() < diff)
                    throw new RuntimeException("Không đủ tồn kho!");
                spct.setSoLuong(spct.getSoLuong() - diff);
            } else if (diff < 0) {
                spct.setSoLuong(spct.getSoLuong() + (-diff));
            }
            sanPhamChiTietRepository.save(spct);

            chiTiet.setSoLuong(newQty);
            chiTiet.setGia(dto.getGia()); // ✅ Thêm dòng này để cập nhật giá
            chiTiet.setThanhTien(dto.getThanhTien());

            DonHangChiTiet saved = chiTietRepository.save(chiTiet);
            donHangService.capNhatTongTienDonHang(chiTiet.getDonHang().getId());

            return convertToDTO(saved);
        }
        return null;
    }
//    public DonHangChiTietDTO update2(int id, DonHangChiTietDTO dto) {
//        Optional<DonHangChiTiet> optional = chiTietRepository.findById(id);
//        if (optional.isPresent()) {
//            DonHangChiTiet chiTiet = optional.get();
//
//
//            // ✅ GIỮ NGUYÊN: Cập nhật thông tin đơn hàng
//            chiTiet.setSoLuong(dto.getSoLuong());
//            chiTiet.setGia(dto.getGia());
//            chiTiet.setThanhTien(dto.getThanhTien());
//
//            DonHangChiTiet saved = chiTietRepository.save(chiTiet);
//            donHangService.capNhatTongTienDonHang2(chiTiet.getDonHang().getId());
//
//            return convertToDTO(saved);
//        }
//        return null;
//    }
public DonHangChiTietDTO update2(int id, DonHangChiTietDTO dto) {
    Optional<DonHangChiTiet> optional = chiTietRepository.findById(id);
    if (optional.isPresent()) {
        DonHangChiTiet chiTiet = optional.get();

        // ✅ THÊM: Kiểm tra tồn kho cho trạng thái 0 (chờ xác nhận)
        int oldQty = chiTiet.getSoLuong();
        int newQty = dto.getSoLuong();
        int diff = newQty - oldQty;

        SanPhamChiTiet spct = chiTiet.getSanPhamChiTiet();

        // Kiểm tra tồn kho khi tăng số lượng
        if (diff > 0) {
            int soLuongDaDat = 0;

            // Lấy tất cả items trong đơn hàng
            List<DonHangChiTiet> allItems = chiTietRepository.findByDonHang_Id(chiTiet.getDonHang().getId());

            // Tính tổng số lượng đã đặt của sản phẩm cùng loại (trừ item hiện tại)
            for (DonHangChiTiet item : allItems) {
                if (!item.getId().equals(id) && item.getSanPhamChiTiet().getId().equals(spct.getId())) {
                    soLuongDaDat += item.getSoLuong();
                }
            }

            // Tổng số lượng sau khi sửa = Số lượng đã đặt (trừ item hiện tại) + Số lượng mới
            int tongSoLuongSeDat = soLuongDaDat + newQty;

            // Kiểm tra: Tồn kho hiện tại >= Tổng số lượng sẽ đặt
            if (spct.getSoLuong() < tongSoLuongSeDat) {
                throw new RuntimeException("Không đủ tồn kho! Tồn kho: " + spct.getSoLuong() +
                        ", Đã đặt (trừ item hiện tại): " + soLuongDaDat +
                        ", Số lượng mới: " + newQty +
                        ", Tổng cần: " + tongSoLuongSeDat);
            }
        }

        // ✅ GIỮ NGUYÊN: Cập nhật thông tin đơn hàng
        chiTiet.setSoLuong(newQty);
        chiTiet.setGia(dto.getGia());
        chiTiet.setThanhTien(dto.getThanhTien());

        DonHangChiTiet saved = chiTietRepository.save(chiTiet);
        donHangService.capNhatTongTienDonHang2(chiTiet.getDonHang().getId());

        return convertToDTO(saved);
    }
    return null;
}

    public void updatePricesForOrder(Integer orderId) {
        DonHang donHang = donHangRepository.findById(orderId)
                .orElseThrow(() -> new RuntimeException("Không tìm thấy đơn hàng"));

        // ✅ SỬA: Chỉ cập nhật giá cho đơn hàng POS (Bán hàng tại quầy) và CHƯA thanh toán
        if (donHang.getLoaiDonHang() != null &&
                "Bán hàng tại quầy".equals(donHang.getLoaiDonHang()) &&
                donHang.getTrangThai() != null &&
                donHang.getTrangThai() == 0) {

            List<DonHangChiTiet> chiTiets = chiTietRepository.findByDonHang_Id(orderId);

            for (DonHangChiTiet chiTiet : chiTiets) {
                if (chiTiet.getSanPhamChiTiet() != null) {
                    SanPhamChiTiet spct = chiTiet.getSanPhamChiTiet();

                    // Lấy giá mới nhất từ sản phẩm - sử dụng giaBanGiamGia thay vì giaBanSauGiam
                    double newPrice;
                    if (spct.getGiaBanGiamGia() != null && spct.getGiaBanGiamGia() < spct.getGiaBan()) {
                        newPrice = spct.getGiaBanGiamGia(); // Giá khuyến mãi
                    } else {
                        newPrice = spct.getGiaBan(); // Giá gốc
                    }

                    // Cập nhật giá và thành tiền
                    chiTiet.setGia(newPrice);
                    chiTiet.setThanhTien(newPrice * chiTiet.getSoLuong());
                }
            }

            chiTietRepository.saveAll(chiTiets);

            // Cập nhật tổng tiền đơn hàng
            donHangService.capNhatTongTienDonHang(orderId);
        }
        // ✅ Nếu đơn hàng không phải POS hoặc đã thanh toán, KHÔNG cập nhật giá
    }


    public void delete(int id) {
        Optional<DonHangChiTiet> optional = chiTietRepository.findById(id);
        if (optional.isPresent()) {
            DonHangChiTiet chiTiet = optional.get();
            SanPhamChiTiet spct = chiTiet.getSanPhamChiTiet();
            // Hoàn lại tồn kho
            spct.setSoLuong(spct.getSoLuong() + chiTiet.getSoLuong());
            sanPhamChiTietRepository.save(spct);

            DonHang donHang = chiTiet.getDonHang();
            if (donHang != null && donHang.getDonHangChiTiets() != null) {
                donHang.getDonHangChiTiets().remove(chiTiet); // Quan trọng!
            }

            chiTietRepository.deleteById(id);
            donHangService.capNhatTongTienDonHang(donHang.getId());
        }
    }
    public void delete2(int id) {
        Optional<DonHangChiTiet> optional = chiTietRepository.findById(id);
        if (optional.isPresent()) {
            DonHangChiTiet chiTiet = optional.get();

            // ✅ SỬA: Không hoàn lại tồn kho nữa
            // if (chiTiet.getDonHang().getTrangThai() >= 1 && chiTiet.getDonHang().getTrangThai() <= 2) {
            //     SanPhamChiTiet spct = chiTiet.getSanPhamChiTiet();
            //     spct.setSoLuong(spct.getSoLuong() + chiTiet.getSoLuong());
            //     sanPhamChiTietRepository.save(spct);
            // }

            // ✅ GIỮ NGUYÊN: Xóa chi tiết đơn hàng
            DonHang donHang = chiTiet.getDonHang();
            if (donHang != null && donHang.getDonHangChiTiets() != null) {
                donHang.getDonHangChiTiets().remove(chiTiet);
            }

            chiTietRepository.deleteById(id);
            donHangService.capNhatTongTienDonHang2(donHang.getId());
        }
    }

//    public void delete2(int id) {
//        Optional<DonHangChiTiet> optional = chiTietRepository.findById(id);
//        if (optional.isPresent()) {
//            DonHangChiTiet chiTiet = optional.get();
//
//            // Chỉ hoàn lại tồn kho khi đơn hàng ở trạng thái 1,2
//            if (chiTiet.getDonHang().getTrangThai() >= 1 && chiTiet.getDonHang().getTrangThai() <= 2) {
//                SanPhamChiTiet spct = chiTiet.getSanPhamChiTiet();
//                spct.setSoLuong(spct.getSoLuong() + chiTiet.getSoLuong());
//                sanPhamChiTietRepository.save(spct);
//            }
//
//            DonHang donHang = chiTiet.getDonHang();
//            if (donHang != null && donHang.getDonHangChiTiets() != null) {
//                donHang.getDonHangChiTiets().remove(chiTiet);
//            }
//
//            chiTietRepository.deleteById(id);
//            donHangService.capNhatTongTienDonHang2(donHang.getId());
//        }
//    }
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

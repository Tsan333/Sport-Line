package com.example.backend.service;

import com.example.backend.dto.NhanVienDTO;
import com.example.backend.dto.PageReSponse;
import com.example.backend.entity.NhanVien;
import com.example.backend.repository.NhanVienRepository;
import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.DateUtil;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.xssf.usermodel.XSSFSheet;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.io.OutputStream;
import java.time.LocalDate;
import java.util.List;

@Service
public class NhanVienService {

    @Autowired
    private NhanVienRepository nhanVienRepository;
    @Autowired
    private PasswordEncoder passwordEncoder;

    // ham convert entity sang dto
    public NhanVienDTO convertDTO(NhanVien nv) {
        return new NhanVienDTO(
                nv.getId(),
                nv.getTenNhanVien(),
                nv.getEmail(),
                nv.getSoDienThoai(),
                nv.getNgaySinh(),
                nv.getGioiTinh(),
                nv.getDiaChi(),
                nv.getVaiTro(),
                nv.getMatKhau(), // Trả về mật khẩu thay vì null
                nv.getCccd(),
                nv.getTrangThai()
        );
    }

    // Validation helper methods
    private boolean isEmailExists(String email, Integer excludeId) {
        if (excludeId != null) {
            return nhanVienRepository.existsByEmailAndIdNot(email, excludeId);
        }
        return nhanVienRepository.existsByEmail(email);
    }

    private boolean isPhoneExists(String phone, Integer excludeId) {
        if (excludeId != null) {
            return nhanVienRepository.existsBySoDienThoaiAndIdNot(phone, excludeId);
        }
        return nhanVienRepository.existsBySoDienThoai(phone);
    }

    private boolean isOver18(LocalDate birthDate) {
        if (birthDate == null) return false;
        LocalDate now = LocalDate.now();
        LocalDate eighteenYearsAgo = now.minusYears(18);
        return birthDate.isBefore(eighteenYearsAgo) || birthDate.isEqual(eighteenYearsAgo);
    }

    // ham lay all nhan vien
    public List<NhanVienDTO> findall() {
        return nhanVienRepository.findAll().stream()
                .map(this::convertDTO) // Sử dụng method convertDTO
                .toList();
    }

    //ham lay danh sach theo id
    public NhanVienDTO findById(Integer id) {
        return nhanVienRepository.findById(id)
                .map(this::convertDTO) // Sử dụng method convertDTO
                .orElse(null);
    }

    // ham create nhanvien
    public NhanVienDTO create(NhanVienDTO dto) {
        // Validation
        if (!isOver18(dto.getNgaySinh())) {
            throw new IllegalArgumentException("Nhân viên phải trên 18 tuổi!");
        }

        if (isEmailExists(dto.getEmail(), null)) {
            throw new IllegalArgumentException("Email đã tồn tại!");
        }

        if (isPhoneExists(dto.getSoDienThoai(), null)) {
            throw new IllegalArgumentException("Số điện thoại đã tồn tại!");
        }

        NhanVien nv = new NhanVien();
        nv.setTenNhanVien(dto.getTenNhanVien());
        nv.setEmail(dto.getEmail());
        nv.setSoDienThoai(dto.getSoDienThoai());
        nv.setNgaySinh(dto.getNgaySinh());
        nv.setGioiTinh(dto.getGioiTinh());
        nv.setDiaChi(dto.getDiaChi());
        nv.setVaiTro(dto.getVaiTro());
        nv.setCccd(dto.getCccd());
        nv.setTrangThai(dto.getTrangThai());
        nv.setMatKhau(dto.getMatKhau()); // Bỏ passwordEncoder.encode()

        return convertDTO(nhanVienRepository.save(nv));
    }

    // ham delete nhan vien
    public boolean delete(Integer id){
        if (nhanVienRepository.existsById(id)) {
            nhanVienRepository.deleteById(id);
            return true;
        }
        return false;
    }

    //ham update nhan vien
    public NhanVienDTO update(int id, NhanVienDTO dto) {
        return nhanVienRepository.findById(id)
                .map(nhanVien -> {
                    // Validation
                    if (!isOver18(dto.getNgaySinh())) {
                        throw new IllegalArgumentException("Nhân viên phải trên 18 tuổi!");
                    }

                    if (isEmailExists(dto.getEmail(), id)) {
                        throw new IllegalArgumentException("Email đã tồn tại!");
                    }

                    if (isPhoneExists(dto.getSoDienThoai(), id)) {
                        throw new IllegalArgumentException("Số điện thoại đã tồn tại!");
                    }

                    nhanVien.setTenNhanVien(dto.getTenNhanVien());
                    nhanVien.setEmail(dto.getEmail());
                    nhanVien.setSoDienThoai(dto.getSoDienThoai());
                    nhanVien.setNgaySinh(dto.getNgaySinh());
                    nhanVien.setGioiTinh(dto.getGioiTinh());
                    nhanVien.setDiaChi(dto.getDiaChi());
                    nhanVien.setVaiTro(dto.getVaiTro());
                    nhanVien.setCccd(dto.getCccd());
                    nhanVien.setTrangThai(dto.getTrangThai());
                    nhanVien.setMatKhau(dto.getMatKhau());

                    return convertDTO(nhanVienRepository.save(nhanVien));
                })
                .orElse(null);
    }

    //phan trang
    public PageReSponse<NhanVienDTO> getPaged(int page, int size) {
        Pageable pageable = PageRequest.of(page, size);
        Page<NhanVien> pageResult = nhanVienRepository.findAll(pageable);

        List<NhanVienDTO> content = pageResult.getContent().stream()
                .map(this::convertDTO) // convert từ Entity sang DTO
                .toList();

        PageReSponse<NhanVienDTO> response = new PageReSponse<>();
        response.setContent(content);
        response.setPageNumber(pageResult.getNumber());
        response.setPageSize(pageResult.getSize());
        response.setTotalElements(pageResult.getTotalElements());
        response.setTotalPages(pageResult.getTotalPages());
        response.setLast(pageResult.isLast());

        return response;
    }

    //tim kiem nhan vien theo ten, sdt, email
    public List<NhanVienDTO> search(String keyword) {
        List<NhanVien> result = nhanVienRepository.search(keyword);
        return result.stream()
                .map(this::convertDTO)
                .toList();
    }

    public void exportExcel(OutputStream out) throws IOException {
        List<NhanVien> list = nhanVienRepository.findAll(); // Lấy dữ liệu từ DB
        XSSFWorkbook workbook = new XSSFWorkbook();
        XSSFSheet sheet = workbook.createSheet("DanhSachNhanVien");

        // �� Dòng tiêu đề giống file import
        Row header = sheet.createRow(0);
        header.createCell(0).setCellValue("Tên nhân viên");
        header.createCell(1).setCellValue("Email");
        header.createCell(2).setCellValue("SĐT");
        header.createCell(3).setCellValue("Ngày sinh");
        header.createCell(4).setCellValue("Giới tính");
        header.createCell(5).setCellValue("Địa chỉ");
        header.createCell(6).setCellValue("Vai trò");
        header.createCell(7).setCellValue("CCCD");
        header.createCell(8).setCellValue("Trạng thái");

        // �� Ghi dữ liệu từng dòng
        int rowIdx = 1;
        for (NhanVien nv : list) {
            Row row = sheet.createRow(rowIdx++);
            row.createCell(0).setCellValue(nv.getTenNhanVien());
            row.createCell(1).setCellValue(nv.getEmail());
            row.createCell(2).setCellValue(nv.getSoDienThoai());
            row.createCell(3).setCellValue(nv.getNgaySinh() != null ? nv.getNgaySinh().toString() : "");

            row.createCell(4).setCellValue(nv.getGioiTinh() != null ?
                    (nv.getGioiTinh() ? "Nam" : "Nữ") : "");

            row.createCell(5).setCellValue(nv.getDiaChi());
            row.createCell(6).setCellValue(nv.getVaiTro() != null ?
                    (nv.getVaiTro() ? "Quản lý" : "Nhân viên") : "");

            row.createCell(7).setCellValue(nv.getCccd());
            row.createCell(8).setCellValue(nv.getTrangThai() != null ?
                    (nv.getTrangThai() ? "Đang hoạt động" : "Tạm khóa") : "");
        }

        // �� Ghi file ra output stream
        workbook.write(out);
        workbook.close();
    }
}
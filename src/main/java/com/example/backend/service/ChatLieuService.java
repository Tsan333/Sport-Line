package com.example.backend.service;


import com.example.backend.entity.ChatLieu;
import com.example.backend.entity.ThuongHieu;
import com.example.backend.repository.ChatLieuInterface;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Locale;
import java.util.Optional;


@Service
public class ChatLieuService {

    @Autowired
    private ChatLieuInterface cli;

    public List<ChatLieu> getAll() {
        return cli.findAllByTrangThai(1);
    }
    public List<ChatLieu> getAllFull() {
        return cli.findAll();
    }
    public List<ChatLieu> searchByName(String name) {
        return cli.findByTenChatLieuContainingIgnoreCase(name);
    }

    public List<ChatLieu> getThungRac() {
        return cli.findAllByTrangThai(0);
    }

    public Optional<ChatLieu> getById(Integer id) {
        return cli.findById(id);
    }

    private String normalizeTenChatLieu(String tenChatLieu) {
        if (tenChatLieu == null) return "";

        return tenChatLieu
                .trim() // Loại bỏ khoảng trắng đầu cuối
                .replaceAll("\\s+", "") // Loại bỏ TẤT CẢ khoảng trắng
                .toLowerCase(Locale.ROOT); // Chuyển về chữ thường với locale chuẩn
    }

    public ResponseEntity<?> create(ChatLieu chatLieu) {
        // THÊM: Chuẩn hóa tên trước khi kiểm tra
        String tenChatLieu = normalizeTenChatLieu(chatLieu.getTenChatLieu());

        if (tenChatLieu.isEmpty()) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("Tên chất liệu không được để trống!");
        }

        //  SỬA: Lấy tất cả chất liệu và so sánh sau khi normalize
        List<ChatLieu> allChatLieu = cli.findAll();
        boolean isDuplicate = allChatLieu.stream()
                .anyMatch(existing -> normalizeTenChatLieu(existing.getTenChatLieu()).equals(tenChatLieu));

        if (isDuplicate) {
            return ResponseEntity.status(HttpStatus.CONFLICT).body("Chất liệu đã tồn tại!");
        }

        //  THÊM: Cập nhật tên đã chuẩn hóa vào entity
        chatLieu.setTenChatLieu(tenChatLieu);
        ChatLieu newChatLieu = cli.save(chatLieu);
        return ResponseEntity.status(HttpStatus.CREATED).body(newChatLieu);
    }

    public ResponseEntity<?> update(Integer id, ChatLieu chatLieu) {
        Optional<ChatLieu> current = cli.findById(id);
        if (current.isEmpty()) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body("Không tìm thấy Chất liệu với ID: " + id);
        }

        //  THÊM: Chuẩn hóa tên trước khi kiểm tra
        String tenChatLieu = normalizeTenChatLieu(chatLieu.getTenChatLieu());

        if (tenChatLieu.isEmpty()) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("Tên chất liệu không được để trống!");
        }

        //  SỬA: Lấy tất cả chất liệu và so sánh sau khi normalize (giống như create)
        List<ChatLieu> allChatLieu = cli.findAll();
        boolean isDuplicate = allChatLieu.stream()
                .anyMatch(existing -> !existing.getId().equals(id) &&
                        normalizeTenChatLieu(existing.getTenChatLieu()).equals(tenChatLieu));

        if (isDuplicate) {
            return ResponseEntity.status(HttpStatus.CONFLICT).body("Tên chất liệu đã tồnại!");
        }

        // THÊM: Cập nhật tên đã chuẩn hóa vào entity
        chatLieu.setTenChatLieu(tenChatLieu);
        chatLieu.setId(id);
        ChatLieu updated = cli.save(chatLieu);
        return ResponseEntity.ok(updated);
    }

    public ResponseEntity<?> delete(Integer id) {
        Optional<ChatLieu> optionalChatLieu = cli.findById(id);
        if (optionalChatLieu.isEmpty()) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body("Chất liệu với ID " + id + " không tìm thấy");
        }

        ChatLieu chatLieu = optionalChatLieu.get();
        chatLieu.setTrangThai(0);
        ChatLieu saved = cli.save(chatLieu);
        return ResponseEntity.ok(saved);
    }
    public void khoiPhucChatLieu(Integer id) {
        ChatLieu cl = cli.findById(id)
                .orElseThrow(() -> new RuntimeException("Không tìm thấy thương hiệu!"));
        cl.setTrangThai(1); // 1 = Đang hoạt động
        cli.save(cl);
    }
}


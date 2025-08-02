package com.example.backend.enums;

import com.fasterxml.jackson.annotation.JsonValue;

public enum TrangThaiDonHang {

    CHO_XAC_NHAN(0, "Chờ xác nhận", "Đơn hàng mới tạo, đang chờ xác nhận"),
    XAC_NHAN(1, "Xác nhận", "Đơn hàng đã được xác nhận"),
    DANG_CHUAN_BI(2, "Đang chuẩn bị", "Đơn hàng đang được chuẩn bị để giao"),
    DANG_GIAO(3, "Đang giao", "Đơn hàng đang trên đường giao đến khách"),
    DA_GIAO(4, "Đã giao", "Giao hàng thành công"),
    DA_HUY(5, "Đã hủy", "Đơn hàng đã bị hủy trước khi giao"),
    TRA_HANG_HOAN_TIEN(6, "Trả hàng / Hoàn tiền", "Khách trả lại hàng hoặc yêu cầu hoàn tiền"),
    GIAO_HANG_KHONG_THANH_CONG(7, "Giao hàng không thành công", "Giao hàng thất bại do không gọi được khách, mất hàng, hoặc khách từ chối thanh toán");

    private final int value;
    private final String displayName;
    private final String description;

    TrangThaiDonHang(int value, String displayName, String description) {
        this.value = value;
        this.displayName = displayName;
        this.description = description;
    }

    public int getValue() {
        return value;
    }

    public String getDisplayName() {
        return displayName;
    }

    public String getDescription() {
        return description;
    }

    public static TrangThaiDonHang fromValue(int value) {
        for (TrangThaiDonHang status : values()) {
            if (status.value == value) {
                return status;
            }
        }
        throw new IllegalArgumentException("Trạng thái không hợp lệ: " + value);
    }

    public boolean isFinalStatus() {
        return this == DA_GIAO || this == DA_HUY || this == TRA_HANG_HOAN_TIEN || this == GIAO_HANG_KHONG_THANH_CONG;
    }

    @JsonValue
    public String toJson() {
        return displayName;
    }
}

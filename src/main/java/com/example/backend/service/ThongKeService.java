package com.example.backend.service;


import com.example.backend.dto.BestSellerProductDTO;
import com.example.backend.dto.RevenueShareDTO;
import com.example.backend.repository.DonHangChiTietRepository;
import com.example.backend.repository.DonHangRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.*;
import java.util.List;

@Service
public class ThongKeService {
    private static final String LOAI_ONLINE = "Online";
    private static final String LOAI_OFFLINE = "Bán hàng tại quầy";
    private static final int STATUS_ONLINE = 4;
    private static final int STATUS_OFFLINE = 1;

    @Autowired
    private DonHangRepository donHangRepository;

    @Autowired
    private DonHangChiTietRepository donHangChiTietRepository;

    // A. Tổng quan
    public Double getRevenueByRange(LocalDate start, LocalDate end) {
        return donHangRepository.sumRevenueAllChannels(start, end);
    }
    public Double getRevenueByMonthYear(int month, int year) {
        LocalDate s = LocalDate.of(year, month, 1);
        LocalDate e = s.withDayOfMonth(s.lengthOfMonth());
        return getRevenueByRange(s, e);
    }
    public Double getTodayRevenue() {
        LocalDate t = LocalDate.now();
        return getRevenueByRange(t, t);
    }
    public Long getTotalProductsSoldByRange(LocalDate start, LocalDate end) {
        return donHangChiTietRepository.sumProductsAllChannels(start, end);
    }
    public Long countCompletedOrdersByRange(LocalDate start, LocalDate end) {
        return donHangRepository.countCompletedOrdersAllChannels(start, end);
    }
    public List<Double> getMonthlyRevenueOfYear(int year) {
        List<Double> result = new java.util.ArrayList<>(12);
        for (int m = 1; m <= 12; m++) result.add(getRevenueByMonthYear(m, year));
        return result;
    }

    // B. Theo kênh
    public Double getRevenueOnline(LocalDate start, LocalDate end) {
        return donHangRepository.sumRevenueByChannel(LOAI_ONLINE, STATUS_ONLINE, start, end);
    }
    public Double getRevenueOffline(LocalDate start, LocalDate end) {
        return donHangRepository.sumRevenueByChannel(LOAI_OFFLINE, STATUS_OFFLINE, start, end);
    }
    public Long getProductsSoldOnline(LocalDate start, LocalDate end) {
        return donHangChiTietRepository.sumProductsByChannel(LOAI_ONLINE, STATUS_ONLINE, start, end);
    }
    public Long getProductsSoldOffline(LocalDate start, LocalDate end) {
        return donHangChiTietRepository.sumProductsByChannel(LOAI_OFFLINE, STATUS_OFFLINE, start, end);
    }

    // C. Nâng cao
    // Bỏ method cũ, tạo method mới đơn giản
    public List<BestSellerProductDTO> getBestSellers(String type) {
        LocalDate start, end;
        switch (type) {
            case "day":
                start = LocalDate.now();
                end = LocalDate.now();
                break;
            case "week":
                start = LocalDate.now().with(DayOfWeek.MONDAY);
                end = LocalDate.now().with(DayOfWeek.SUNDAY);
                break;
            case "month":
            default:
                start = LocalDate.now().withDayOfMonth(1);
                end = LocalDate.now().withDayOfMonth(LocalDate.now().lengthOfMonth());
                break;
            case "year":
                start = LocalDate.now().withDayOfYear(1);
                end = LocalDate.now().withDayOfYear(LocalDate.now().lengthOfYear());
                break;
        }

        // Luôn lấy từ cả 2 kênh
        return donHangChiTietRepository.findBestSellersAllChannels(start, end);
    }

// Bỏ method cũ này
// public List<BestSellerProductDTO> getBestSellersByChannel(String channel, LocalDate start, LocalDate end) { ... }

    public Long countOrdersByDate(LocalDate date) {
        return donHangRepository.countOrdersByDate(date);
    }
    public RevenueShareDTO getRevenueShare(LocalDate start, LocalDate end) {
        double online = getRevenueOnline(start, end);
        double offline = getRevenueOffline(start, end);
        double total = online + offline;
        double onlinePct = total == 0 ? 0 : (online / total) * 100.0;
        double offlinePct = total == 0 ? 0 : (offline / total) * 100.0;
        return new RevenueShareDTO(online, offline, onlinePct, offlinePct);
    }

}

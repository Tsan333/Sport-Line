package com.example.backend.service;


import com.example.backend.dto.BestSellerProductDTO;
import com.example.backend.repository.DonHangChiTietRepository;
import com.example.backend.repository.DonHangRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.*;
import java.util.List;

@Service
public class ThongKeService {

    @Autowired
    private DonHangRepository donHangRepository;

    @Autowired
    private DonHangChiTietRepository donHangChiTietRepository;

    public Double getRevenueByMonthYear(int month, int year) {
        LocalDateTime start = LocalDateTime.of(year, month, 1, 0, 0);
        LocalDateTime end = start.withDayOfMonth(start.toLocalDate().lengthOfMonth()).withHour(23).withMinute(59).withSecond(59);
        return donHangRepository.sumRevenueBetweenDates(start, end);
    }

    public Double getTodayRevenue() {
        LocalDateTime start = LocalDate.now().atStartOfDay();
        LocalDateTime end = start.withHour(23).withMinute(59).withSecond(59);
        return donHangRepository.sumRevenueBetweenDates(start, end);
    }

    public Long getTotalProductsSold() {
        return donHangChiTietRepository.sumTotalProductsSold();
    }

    public List<BestSellerProductDTO> getBestSellers(String type) {
        LocalDateTime now = LocalDateTime.now();
        LocalDateTime start;

        switch (type.toLowerCase()) {
            case "day":
                start = now.toLocalDate().atStartOfDay();
                break;
            case "week":
                start = now.toLocalDate().with(DayOfWeek.MONDAY).atStartOfDay();
                break;
            case "month":
                start = now.toLocalDate().withDayOfMonth(1).atStartOfDay();
                break;
            case "year":
                start = now.toLocalDate().withDayOfYear(1).atStartOfDay();
                break;
            default:
                throw new IllegalArgumentException("Invalid type: " + type);
        }

        return donHangChiTietRepository.findBestSellers(start, now);
    }
}

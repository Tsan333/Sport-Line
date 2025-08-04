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

//    public Double getRevenueByMonthYear(int month, int year) {
//        LocalDateTime start = LocalDateTime.of(year, month, 1, 0, 0);
//        LocalDateTime end = start.withDayOfMonth(start.toLocalDate().lengthOfMonth()).withHour(23).withMinute(59).withSecond(59);
//        return donHangRepository.sumRevenueBetweenDates(start, end);
//    }

    public Double getTodayRevenue() {
        LocalDate today = LocalDate.now();
        return donHangRepository.sumRevenueBetweenDates(today, today);
    }

    public Double getRevenueByMonthYear(int month, int year) {
        LocalDate start = LocalDate.of(year, month, 1);
        LocalDate end = start.withDayOfMonth(start.lengthOfMonth());
        return donHangRepository.sumRevenueBetweenDates(start, end);
    }

    public Long getTotalProductsSold() {
        return donHangChiTietRepository.sumTotalProductsSold();
    }

    public List<BestSellerProductDTO> getBestSellers(String type) {
        LocalDate now = LocalDate.now();
        LocalDate start;

        switch (type.trim().toLowerCase()) {
            case "day":
                start = now;
                break;
            case "week":
                start = now.with(DayOfWeek.MONDAY);
                break;
            case "month":
                start = now.withDayOfMonth(1);
                break;
            case "year":
                start = now.withDayOfYear(1);
                break;
            default:
                throw new IllegalArgumentException("Invalid type: " + type);
        }

        return donHangChiTietRepository.findBestSellers(start, now);
    }

}

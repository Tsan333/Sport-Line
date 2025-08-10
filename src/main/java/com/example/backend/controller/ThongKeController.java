package com.example.backend.controller;

import com.example.backend.dto.BestSellerProductDTO;
import com.example.backend.dto.RevenueShareDTO;
import com.example.backend.service.ThongKeService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.time.DayOfWeek;
import java.time.LocalDate;
import java.util.List;

@RestController
@RequestMapping("/api/thong-ke")
public class ThongKeController {
    @Autowired private ThongKeService thongKeService;

    // A. Tổng quan
    @GetMapping("/today-revenue")
    public Double getTodayRevenue() { return thongKeService.getTodayRevenue(); }

    @GetMapping("/revenue")
    public Double getRevenueByMonthYear(@RequestParam int month, @RequestParam int year) {
        return thongKeService.getRevenueByMonthYear(month, year);
    }

    @GetMapping("/revenue-monthly")
    public List<Double> getRevenueMonthly(@RequestParam int year) {
        return thongKeService.getMonthlyRevenueOfYear(year);
    }

    @GetMapping("/products-sold")
    public Long getTotalProductsSold(@RequestParam int month, @RequestParam int year) {
        LocalDate s = LocalDate.of(year, month, 1);
        LocalDate e = s.withDayOfMonth(s.lengthOfMonth());
        return thongKeService.getTotalProductsSoldByRange(s, e);
    }

    @GetMapping("/orders-completed")
    public Long getOrdersCompleted(@RequestParam int month, @RequestParam int year) {
        LocalDate s = LocalDate.of(year, month, 1);
        LocalDate e = s.withDayOfMonth(s.lengthOfMonth());
        return thongKeService.countCompletedOrdersByRange(s, e);
    }

    // B. Theo kênh
    @GetMapping("/revenue-by-channel")
    public Double getRevenueByChannel(@RequestParam String channel,
                                      @RequestParam int month, @RequestParam int year) {
        LocalDate s = LocalDate.of(year, month, 1);
        LocalDate e = s.withDayOfMonth(s.lengthOfMonth());
        return "ONLINE".equalsIgnoreCase(channel)
                ? thongKeService.getRevenueOnline(s, e)
                : thongKeService.getRevenueOffline(s, e);
    }

    @GetMapping("/products-sold-by-channel")
    public Long getProductsSoldByChannel(@RequestParam String channel,
                                         @RequestParam int month, @RequestParam int year) {
        LocalDate s = LocalDate.of(year, month, 1);
        LocalDate e = s.withDayOfMonth(s.lengthOfMonth());
        return "ONLINE".equalsIgnoreCase(channel)
                ? thongKeService.getProductsSoldOnline(s, e)
                : thongKeService.getProductsSoldOffline(s, e);
    }

    // C. Nâng cao
    // Bỏ parameter channel, chỉ giữ type
    @GetMapping("/best-sellers")
    public List<BestSellerProductDTO> getBestSellers(
            @RequestParam(defaultValue = "month") String type  // Chỉ giữ type
    ) {
        LocalDate now = LocalDate.now();
        LocalDate start = switch (type.trim().toLowerCase()) {
            case "day" -> now;
            case "week" -> now.with(DayOfWeek.MONDAY);
            case "month" -> now.withDayOfMonth(1);
            case "year" -> now.withDayOfYear(1);
            default -> now.withDayOfMonth(1);
        };
        // Gọi trực tiếp method mới, không cần channel
        return thongKeService.getBestSellers(type);
    }

    @GetMapping("/revenue-share")
    public RevenueShareDTO getRevenueShare(@RequestParam int month, @RequestParam int year) {
        LocalDate s = LocalDate.of(year, month, 1);
        LocalDate e = s.withDayOfMonth(s.lengthOfMonth());
        return thongKeService.getRevenueShare(s, e);
    }

    //lọc số đơn đã bán theo ngày
    @GetMapping("/orders-by-date")
    public Long getOrdersByDate(@RequestParam String date) {
        LocalDate localDate = LocalDate.parse(date);
        return thongKeService.countOrdersByDate(localDate);
    }
}
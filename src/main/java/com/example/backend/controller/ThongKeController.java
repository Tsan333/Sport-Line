package com.example.backend.controller;

import com.example.backend.dto.BestSellerProductDTO;
import com.example.backend.service.ThongKeService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/api/thong-ke")
public class ThongKeController {

    @Autowired
    private ThongKeService thongKeService;

    @GetMapping("/revenue")
    public Double getRevenueByMonthYear(@RequestParam int month, @RequestParam int year) {
        return thongKeService.getRevenueByMonthYear(month, year);
    }

    @GetMapping("/today-revenue")
    public Double getTodayRevenue() {
        return thongKeService.getTodayRevenue();
    }

    @GetMapping("/products-sold")
    public Long getTotalProductsSold() {
        return thongKeService.getTotalProductsSold();
    }

    @GetMapping("/best-sellers")
    public List<BestSellerProductDTO> getBestSellers(@RequestParam String type) {
        return thongKeService.getBestSellers(type);
    }
}

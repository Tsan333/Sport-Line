package com.example.backend.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

// RevenueShareDTO.java
@Data
@AllArgsConstructor
@NoArgsConstructor
public class RevenueShareDTO {
    private double onlineRevenue;
    private double offlineRevenue;
    private double onlinePercent;
    private double offlinePercent;
}

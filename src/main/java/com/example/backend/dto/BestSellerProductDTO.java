package com.example.backend.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@AllArgsConstructor
@NoArgsConstructor
@Setter
@Getter
public class BestSellerProductDTO {
    private Integer productId;        // Thay vì productDetailId
    private String productName;
    private String brandName;
    private Long totalSold;
    private String images;
}
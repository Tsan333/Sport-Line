package com.example.backend.controller;

import com.example.backend.service.GHNClientService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.*;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.client.RestTemplate;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/giaohang")
public class GiaoHangController {

    @Autowired
    private GHNClientService ghnClientService;

    @Value("${ghn.token}")
    private String ghnToken;

    @Autowired
    private RestTemplate restTemplate;

    @Value("${ghn.shopId}")
    private Integer ghnShopId;

    // API tính phí ship
    @PostMapping("/tinh-phi-ship")
    public ResponseEntity<Map<String, Object>> tinhPhiShip(@RequestBody Map<String, Object> request) {
        try {
            Integer toDistrictId = (Integer) request.get("toDistrictId");
            String toWardCode = (String) request.get("toWardCode");
            Integer weightGram = (Integer) request.get("weightGram");

            if (toDistrictId == null || toWardCode == null || weightGram == null) {
                return ResponseEntity.badRequest().body(Map.of(
                        "error", "Thiếu thông tin: toDistrictId, toWardCode, weightGram"
                ));
            }

            int phiShip = ghnClientService.tinhPhiVanChuyen(toDistrictId, toWardCode, weightGram);

            return ResponseEntity.ok(Map.of(
                    "phiVanChuyen", phiShip,
                    "toDistrictId", toDistrictId,
                    "toWardCode", toWardCode,
                    "weightGram", weightGram
            ));

        } catch (Exception e) {
            return ResponseEntity.internalServerError().body(Map.of(
                    "error", "Lỗi khi tính phí ship: " + e.getMessage()
            ));
        }
    }

    @GetMapping("/districts")
    public ResponseEntity<List<Map<String, Object>>> getDistricts() {
        // ✅ THỬ: Các endpoint khác cho GHN production
        String[] urls = {
                "https://online-gateway.ghn.vn/shiip/public-api/v2/master-data/district",
                "https://online-gateway.ghn.vn/shiip/public-api/v2/district",
                "https://online-gateway.ghn.vn/shiip/public-api/v2/master-data/district",
                "https://online-gateway.ghn.vn/shiip/public-api/v2/district"
        };

        for (String url : urls) {
            try {
                HttpHeaders headers = new HttpHeaders();
                headers.set("Token", ghnToken);

                HttpEntity<String> request = new HttpEntity<>(headers);
                ResponseEntity<Map> response = restTemplate.exchange(url, HttpMethod.GET, request, Map.class);

                if (response.getStatusCode() == HttpStatus.OK) {
                    Map<String, Object> responseBody = response.getBody();
                    if (responseBody != null && responseBody.get("data") instanceof List) {
                        return ResponseEntity.ok((List<Map<String, Object>>) responseBody.get("data"));
                    }
                }
            } catch (Exception e) {
                System.out.println("❌ Lỗi với " + url + ": " + e.getMessage());
            }
        }

        // ✅ Nếu tất cả đều lỗi, trả về mock data
        List<Map<String, Object>> mockDistricts = List.of(
                Map.of("id", 1442, "name", "Hà Nội", "code", "HN"),
                Map.of("id", 2025, "name", "TP.HCM", "code", "HCM"),
                Map.of("id", 1444, "name", "Đà Nẵng", "code", "DN"),
                Map.of("id", 1446, "name", "Hải Phòng", "code", "HP"),
                Map.of("id", 1448, "name", "Cần Thơ", "code", "CT")
        );

        return ResponseEntity.ok(mockDistricts);
    }

    @GetMapping("/test-token")
    public ResponseEntity<String> testToken() {
        return ResponseEntity.ok("Token: " + ghnToken + ", ShopId: " + ghnShopId);
    }

    @GetMapping("/test")
    public ResponseEntity<String> test() {
        return ResponseEntity.ok("GHN API đang hoạt động!");
    }
}
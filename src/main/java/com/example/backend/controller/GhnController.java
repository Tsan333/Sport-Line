package com.example.backend.controller;

import com.example.backend.service.GhnService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.Map;

@RestController
@RequestMapping("/api/ghn")
@CrossOrigin(origins = {"http://localhost:3000", "http://localhost:3001"})
public class GhnController {

    private final GhnService ghnService;

    public GhnController(GhnService ghnService) {
        this.ghnService = ghnService;
    }

    // ✅ Lấy danh sách tỉnh/thành
    @GetMapping("/provinces")
    public ResponseEntity<Object> getProvinces() {
        try {
            Object result = ghnService.getProvinces();
            return ResponseEntity.ok(result);
        } catch (Exception e) {
            System.err.println("Error getting provinces: " + e.getMessage());
            e.printStackTrace();
            return ResponseEntity.status(500).body(Map.of("error", "Failed to get provinces"));
        }
    }

    // ✅ Lấy danh sách quận/huyện theo provinceId
    @GetMapping("/districts/{provinceId}")
    public ResponseEntity<Object> getDistricts(@PathVariable Integer provinceId) {
        try {
            Object result = ghnService.getDistricts(provinceId);
            return ResponseEntity.ok(result);
        } catch (Exception e) {
            System.err.println("Error getting districts: " + e.getMessage());
            e.printStackTrace();
            return ResponseEntity.status(500).body(Map.of("error", "Failed to get districts"));
        }
    }

    // ✅ Lấy danh sách phường/xã theo districtId
    @GetMapping("/wards/{districtId}")
    public ResponseEntity<Object> getWards(@PathVariable Integer districtId) {
        try {
            Object result = ghnService.getWards(districtId);
            return ResponseEntity.ok(result);
        } catch (Exception e) {
            System.err.println("Error getting wards: " + e.getMessage());
            e.printStackTrace();
            return ResponseEntity.status(500).body(Map.of("error", "Failed to get wards"));
        }
    }

    // ✅ Lấy danh sách dịch vụ khả dụng
    @GetMapping("/available-services")
    public ResponseEntity<Object> getAvailableServices(@RequestParam Integer fromDistrict,
                                                       @RequestParam Integer toDistrict) {
        try {
            Object result = ghnService.getAvailableServices(fromDistrict, toDistrict);
            return ResponseEntity.ok(result);
        } catch (Exception e) {
            System.err.println("Error getting available services: " + e.getMessage());
            e.printStackTrace();
            return ResponseEntity.status(500).body(Map.of("error", "Failed to get available services"));
        }
    }

    // ✅ Tính phí vận chuyển
    @PostMapping("/calculate-fee")
    public ResponseEntity<Object> calculateFee(@RequestBody Map<String, Object> request) {
        try {
            System.out.println("Received calculate-fee request: " + request);
            Object result = ghnService.calculateFee(request);
            System.out.println("Calculate-fee result: " + result);
            return ResponseEntity.ok(result);
        } catch (Exception e) {
            System.err.println("Error calculating fee: " + e.getMessage());
            e.printStackTrace();
            return ResponseEntity.status(500).body(Map.of("error", "Failed to calculate fee: " + e.getMessage()));
        }
    }

    // ✅ Test endpoint với dữ liệu cố định
    @GetMapping("/test-calculate-fee")
    public ResponseEntity<Object> testCalculateFee() {
        try {
            Map<String, Object> testRequest = new HashMap<>();
            testRequest.put("fromDistrict", 1484); // Ba Đình, Hà Nội - địa chỉ shop thực tế (DistrictID chính xác)
            testRequest.put("toDistrict", 2025);   // TP.HCM - Quận 1
            testRequest.put("toWardCode", "90737"); // Phường Phúc Xá
            testRequest.put("weight", 500);

            System.out.println("Testing calculate-fee with fixed data: " + testRequest);
            Object result = ghnService.calculateFee(testRequest);
            System.out.println("Test result: " + result);
            return ResponseEntity.ok(result);
        } catch (Exception e) {
            System.err.println("Error in test calculate fee: " + e.getMessage());
            e.printStackTrace();
            return ResponseEntity.status(500).body(Map.of("error", "Test failed: " + e.getMessage()));
        }
    }

    // ✅ Test endpoint với district ID khác
    @GetMapping("/test-calculate-fee-alt")
    public ResponseEntity<Object> testCalculateFeeAlternative() {
        try {
            Map<String, Object> testRequest = new HashMap<>();
            testRequest.put("fromDistrict", 1484); // Ba Đình, Hà Nội - địa chỉ shop thực tế (DistrictID chính xác)
            testRequest.put("toDistrict", 1484);   // Hà Nội - Quận Ba Đình (nội thành)
            testRequest.put("toWardCode", "1A0106"); // Phường Liễu Giai (Láng Hạ)
            testRequest.put("weight", 500);

            System.out.println("Testing calculate-fee with alternative data: " + testRequest);
            Object result = ghnService.calculateFee(testRequest);
            System.out.println("Alternative test result: " + result);
            return ResponseEntity.ok(result);
        } catch (Exception e) {
            System.err.println("Error in alternative test calculate fee: " + e.getMessage());
            e.printStackTrace();
            return ResponseEntity.status(500).body(Map.of("error", "Alternative test failed: " + e.getMessage()));
        }
    }

    // ✅ Test available services trực tiếp
    @GetMapping("/test-available-services")
    public ResponseEntity<Object> testAvailableServices() {
        try {
            System.out.println("Testing available services for Ba Đình, Hà Nội -> TP.HCM");
            Object result = ghnService.getAvailableServices(1484, 2025);
            System.out.println("Available services result: " + result);
            return ResponseEntity.ok(result);
        } catch (Exception e) {
            System.err.println("Error in test available services: " + e.getMessage());
            e.printStackTrace();
            return ResponseEntity.status(500).body(Map.of("error", "Available services test failed: " + e.getMessage()));
        }
    }

    // ✅ Test available services cho Hà Nội nội thành
    @GetMapping("/test-available-services-hanoi")
    public ResponseEntity<Object> testAvailableServicesHanoi() {
        try {
            System.out.println("Testing available services for Ba Đình, Hà Nội nội thành");
            Object result = ghnService.getAvailableServices(1484, 1484);
            System.out.println("Available services Hanoi result: " + result);
            return ResponseEntity.ok(result);
        } catch (Exception e) {
            System.err.println("Error in test available services Hanoi: " + e.getMessage());
            e.printStackTrace();
            return ResponseEntity.status(500).body(Map.of("error", "Available services Hanoi test failed: " + e.getMessage()));
        }
    }
}
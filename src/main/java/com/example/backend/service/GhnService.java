package com.example.backend.service;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.*;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import java.util.HashMap;
import java.util.Map;

@Service
public class GhnService {

    @Value("${ghn.token}")
    private String token;

    @Value("${ghn.shopId}")
    private Integer shopId;

    @Value("${ghn.baseUrl}")
    private String baseUrl;

    private final RestTemplate restTemplate = new RestTemplate();

    private HttpHeaders createHeaders() {
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);
        headers.set("Token", token);
        headers.set("ShopId", String.valueOf(shopId));
        return headers;
    }

    // ✅ Lấy danh sách tỉnh/thành
    public Object getProvinces() {
        try {
            String url = baseUrl + "/master-data/province";
            System.out.println("Calling GHN provinces API: " + url);
            HttpEntity<String> entity = new HttpEntity<>(createHeaders());
            ResponseEntity<Object> response = restTemplate.exchange(url, HttpMethod.GET, entity, Object.class);
            System.out.println("Provinces API response: " + response.getBody());
            return response.getBody();
        } catch (Exception e) {
            System.err.println("Error in getProvinces: " + e.getMessage());
            e.printStackTrace();
            throw e;
        }
    }

    // ✅ Lấy danh sách quận/huyện
    public Object getDistricts(Integer provinceId) {
        try {
            String url = baseUrl + "/master-data/district";
            Map<String, Object> body = new HashMap<>();
            body.put("province_id", provinceId);

            System.out.println("Calling GHN districts API: " + url + " with body: " + body);
            HttpEntity<Map<String, Object>> entity = new HttpEntity<>(body, createHeaders());
            ResponseEntity<Object> response = restTemplate.exchange(url, HttpMethod.POST, entity, Object.class);
            System.out.println("Districts API response: " + response.getBody());
            return response.getBody();
        } catch (Exception e) {
            System.err.println("Error in getDistricts: " + e.getMessage());
            e.printStackTrace();
            throw e;
        }
    }

    // ✅ Lấy danh sách phường/xã
    public Object getWards(Integer districtId) {
        try {
            String url = baseUrl + "/master-data/ward";
            Map<String, Object> body = new HashMap<>();
            body.put("district_id", districtId);

            System.out.println("Calling GHN wards API: " + url + " with body: " + body);
            HttpEntity<Map<String, Object>> entity = new HttpEntity<>(body, createHeaders());
            ResponseEntity<Object> response = restTemplate.exchange(url, HttpMethod.POST, entity, Object.class);
            System.out.println("Wards API response: " + response.getBody());
            return response.getBody();
        } catch (Exception e) {
            System.err.println("Error in getWards: " + e.getMessage());
            e.printStackTrace();
            throw e;
        }
    }

    // ✅ Lấy danh sách dịch vụ khả dụng (để có service_id)
    public Object getAvailableServices(Integer fromDistrict, Integer toDistrict) {
        try {
            String url = baseUrl + "/v2/shipping-order/available-services";

            Map<String, Object> body = new HashMap<>();
            body.put("shop_id", shopId);
            body.put("from_district", fromDistrict);
            body.put("to_district", toDistrict);

            System.out.println("Calling GHN available-services API: " + url + " with body: " + body);
            HttpEntity<Map<String, Object>> entity = new HttpEntity<>(body, createHeaders());
            ResponseEntity<Object> response = restTemplate.exchange(url, HttpMethod.POST, entity, Object.class);
            System.out.println("Available services API response: " + response.getBody());
            return response.getBody();
        } catch (Exception e) {
            System.err.println("Error in getAvailableServices: " + e.getMessage());
            e.printStackTrace();
            throw e;
        }
    }

    // ✅ Tính phí vận chuyển - SỬA: Lấy service_id trước
    public Object calculateFee(Map<String, Object> request) {
        try {
            // ✅ BƯỚC 1: Lấy service_id trước
            Integer fromDistrict = (Integer) request.get("fromDistrict");
            Integer toDistrict = (Integer) request.get("toDistrict");

            System.out.println("Step 1: Getting available services for fromDistrict=" + fromDistrict + ", toDistrict=" + toDistrict);

            Object availableServices = getAvailableServices(fromDistrict, toDistrict);
            System.out.println("Available services result: " + availableServices);

            // ✅ BƯỚC 2: Lấy service_id đầu tiên từ danh sách dịch vụ
            Integer serviceId = null;
            System.out.println("Available services type: " + (availableServices != null ? availableServices.getClass().getName() : "null"));

            if (availableServices instanceof Map) {
                Map<String, Object> servicesMap = (Map<String, Object>) availableServices;
                System.out.println("Services map keys: " + servicesMap.keySet());

                if (servicesMap.containsKey("data")) {
                    Object dataObj = servicesMap.get("data");
                    System.out.println("Data object type: " + (dataObj != null ? dataObj.getClass().getName() : "null"));

                    if (dataObj instanceof Map) {
                        Map<String, Object> data = (Map<String, Object>) dataObj;
                        System.out.println("Data keys: " + data.keySet());

                        if (data.containsKey("service_id")) {
                            serviceId = (Integer) data.get("service_id");
                            System.out.println("Found service_id in data: " + serviceId);
                        } else if (data.containsKey("services") && data.get("services") instanceof java.util.List) {
                            java.util.List<?> services = (java.util.List<?>) data.get("services");
                            System.out.println("Services list size: " + services.size());

                            if (!services.isEmpty() && services.get(0) instanceof Map) {
                                Map<String, Object> firstService = (Map<String, Object>) services.get(0);
                                System.out.println("First service keys: " + firstService.keySet());

                                if (firstService.containsKey("service_id")) {
                                    serviceId = (Integer) firstService.get("service_id");
                                    System.out.println("Found service_id in first service: " + serviceId);
                                }
                            }
                        }
                    } else if (dataObj instanceof java.util.List) {
                        java.util.List<?> services = (java.util.List<?>) dataObj;
                        System.out.println("Services list size: " + services.size());

                        if (!services.isEmpty() && services.get(0) instanceof Map) {
                            Map<String, Object> firstService = (Map<String, Object>) services.get(0);
                            System.out.println("First service keys: " + firstService.keySet());

                            if (firstService.containsKey("service_id")) {
                                serviceId = (Integer) firstService.get("service_id");
                                System.out.println("Found service_id in first service: " + serviceId);
                            }
                        }
                    }
                }
            }

            if (serviceId == null) {
                // Fallback: sử dụng service_id mặc định cho Express
                serviceId = 53320; // Express service
                System.out.println("Warning: Could not get service_id from available services, using default: " + serviceId);
            } else {
                System.out.println("Using service_id: " + serviceId);
            }

            // ✅ BƯỚC 3: Tính phí với service_id
            String url = baseUrl + "/v2/shipping-order/fee";

            // GHN bắt buộc cần shop_id và service_id
            request.put("shop_id", shopId);
            request.put("service_id", serviceId);

            // ✅ SỬA: Đổi tên field để phù hợp với GHN API
            if (request.containsKey("fromDistrict")) {
                request.put("from_district", request.remove("fromDistrict"));
            }
            if (request.containsKey("toDistrict")) {
                request.put("to_district", request.remove("toDistrict"));
            }
            if (request.containsKey("toWardCode")) {
                request.put("to_ward_code", request.remove("toWardCode"));
            }

            System.out.println("Step 2: Calling GHN calculate-fee API: " + url);
            System.out.println("Request body: " + request);
            System.out.println("Headers: Token=" + token + ", ShopId=" + shopId);

            HttpEntity<Map<String, Object>> entity = new HttpEntity<>(request, createHeaders());
            ResponseEntity<Object> response = restTemplate.exchange(url, HttpMethod.POST, entity, Object.class);
            System.out.println("Calculate fee API response: " + response.getBody());
            return response.getBody();
        } catch (Exception e) {
            System.err.println("Error in calculateFee: " + e.getMessage());
            e.printStackTrace();
            throw e;
        }
    }
}
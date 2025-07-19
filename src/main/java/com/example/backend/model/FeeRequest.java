package com.example.backend.model;

import lombok.Data;

@Data
public class FeeRequest {
    private int service_type_id;
    private int insurance_value;
    private int from_district_id;
    private int to_district_id;
    private String to_ward_code;
    private int height;
    private int length;
    private int weight;
    private int width;
}

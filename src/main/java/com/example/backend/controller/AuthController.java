package com.example.backend.controller;

import com.example.backend.dto.AuthResponse;
import com.example.backend.dto.DangNhapRequest;
import com.example.backend.service.AuthService;
import com.google.api.client.googleapis.auth.oauth2.GoogleIdToken;
import com.google.api.client.googleapis.auth.oauth2.GoogleIdToken.Payload;
import com.google.api.client.googleapis.auth.oauth2.GoogleIdTokenVerifier;
import com.google.api.client.http.javanet.NetHttpTransport;
import com.google.api.client.json.jackson2.JacksonFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.Collections;
import java.util.HashMap;
import java.util.Map;

@RestController
@RequestMapping("/api/auth")
@CrossOrigin(origins = "http://localhost:3000")
public class AuthController {

    @Autowired
    private AuthService authService;

    // Google OAuth Client ID - sử dụng từ application.properties
    private static final String GOOGLE_CLIENT_ID = "326799010600-sqfvc012vkhmkt52bbaemnq3000ps7a4.apps.googleusercontent.com";

    @PostMapping("/dang-nhap")
    public ResponseEntity<AuthResponse> dangNhap(@RequestBody DangNhapRequest req) {
        AuthResponse res = authService.dangNhap(req);
        return ResponseEntity.ok(res);
    }

    @PostMapping("/google")
    public ResponseEntity<?> googleLogin(@RequestBody GoogleLoginRequest request) {
        try {
            // Tạo verifier để xác thực Google ID token
            GoogleIdTokenVerifier verifier = new GoogleIdTokenVerifier.Builder(new NetHttpTransport(), new JacksonFactory())
                    .setAudience(Collections.singletonList(GOOGLE_CLIENT_ID))
                    .build();

            // Xác thực token
            GoogleIdToken idToken = verifier.verify(request.getCredential());

            if (idToken != null) {
                Payload payload = idToken.getPayload();

                // Lấy thông tin user từ payload
                String email = payload.getEmail();
                String name = (String) payload.get("name");
                String picture = (String) payload.get("picture");

                // Tích hợp với AuthService hiện có
                AuthResponse res = authService.dangNhapGoogle(email, name, picture);

                return ResponseEntity.ok(res);
            } else {
                return ResponseEntity.badRequest().body(Map.of(
                        "success", false,
                        "message", "Token Google không hợp lệ"
                ));
            }

        } catch (Exception e) {
            e.printStackTrace();
            return ResponseEntity.badRequest().body(Map.of(
                    "success", false,
                    "message", "Lỗi xác thực Google: " + e.getMessage()
            ));
        }
    }
}

// DTO cho Google OAuth request
class GoogleLoginRequest {
    private String credential;

    public String getCredential() {
        return credential;
    }

    public void setCredential(String credential) {
        this.credential = credential;
    }
}
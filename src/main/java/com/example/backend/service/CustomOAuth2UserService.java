package com.example.backend.service;

import com.example.backend.entity.KhachHang;
import com.example.backend.repository.KhachHangRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.oauth2.client.userinfo.DefaultOAuth2UserService;
import org.springframework.security.oauth2.client.userinfo.OAuth2UserRequest;
import org.springframework.security.oauth2.core.user.OAuth2User;
import org.springframework.stereotype.Service;

import java.util.UUID;

@Service
@RequiredArgsConstructor
public class CustomOAuth2UserService extends DefaultOAuth2UserService {

    private final KhachHangRepository khachHangRepository;
    private final PasswordEncoder passwordEncoder;

    @Override
    public OAuth2User loadUser(OAuth2UserRequest request) {
        OAuth2User user = super.loadUser(request);

        String email = user.getAttribute("email");
        String name = user.getAttribute("name");

        KhachHang khachHang = khachHangRepository.findByEmail(email).orElse(null);
        if (khachHang == null) {
            khachHang = KhachHang.builder()
                    .email(email)
                    .tenKhachHang(name)
                    .matKhau(passwordEncoder.encode(UUID.randomUUID().toString()))
                    .trangThai(true)

                    .build();
            khachHangRepository.save(khachHang);
        }

        return user;
    }
}

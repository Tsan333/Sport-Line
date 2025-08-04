package com.example.backend.service;

import com.example.backend.config.VNpayConfig;
import com.example.backend.model.VNPayUtil;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.stereotype.Service;

import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import java.util.*;

@Service
public class PaymentService {

    @Autowired
    private VNpayConfig vnpayConfig;

    @Autowired
    private JavaMailSender mailSender;

    public String createPaymentUrl(int amount, String ipAddress) throws Exception {
        Map<String, String> vnpParams = vnpayConfig.createVNPayParams(amount, ipAddress);

        List<String> fieldNames = new ArrayList<>(vnpParams.keySet());
        Collections.sort(fieldNames);

        StringBuilder hashData = new StringBuilder();
        StringBuilder query = new StringBuilder();

        for (String fieldName : fieldNames) {
            String value = vnpParams.get(fieldName);
            if (value != null && !value.isEmpty()) {
                hashData.append(fieldName).append('=').append(URLEncoder.encode(value, StandardCharsets.US_ASCII)).append('&');
                query.append(fieldName).append('=').append(URLEncoder.encode(value, StandardCharsets.US_ASCII)).append('&');
            }
        }

        hashData.setLength(hashData.length() - 1);
        query.setLength(query.length() - 1);

        String secureHash = VNPayUtil.hmacSHA512(vnpayConfig.getSecretKey(), hashData.toString());
        return vnpayConfig.getPayUrl() + "?" + query + "&vnp_SecureHash=" + secureHash;
    }

    public String processReturn(HttpServletRequest request) {
        String vnp_ResponseCode = request.getParameter("vnp_ResponseCode");
        String vnp_TxnRef = request.getParameter("vnp_TxnRef");
        String amount = request.getParameter("vnp_Amount");

        if ("00".equals(vnp_ResponseCode)) {
            sendSuccessEmail(vnp_TxnRef, amount);
            return "Thanh toán thành công. Mã giao dịch: " + vnp_TxnRef;
        }
        return "Thanh toán thất bại. Mã: " + vnp_ResponseCode;
    }

    private void sendSuccessEmail(String txnRef, String amount) {
        SimpleMailMessage message = new SimpleMailMessage();
        message.setTo("ductai13131010@gmail.com"); // bạn có thể cho động luôn
        message.setSubject("Giao dịch thành công với VNPay");
        message.setText("Giao dịch mã: " + txnRef + "\nSố tiền: " + (Integer.parseInt(amount) / 100) + " VNĐ\nCảm ơn bạn đã sử dụng dịch vụ!");
        mailSender.send(message);
    }
}

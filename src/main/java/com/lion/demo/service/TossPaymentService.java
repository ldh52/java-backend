package com.lion.demo.service;

import com.lion.demo.entity.PaymentApproveRequest;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.client.HttpClientErrorException;
import org.springframework.web.client.RestTemplate;

import java.util.HashMap;
import java.util.Map;

@Service
public class TossPaymentService {
    @Autowired private RestTemplate restTemplate;
    @Value("${toss.payment.secret.key}") private String SECRET_KEY;
    private final String API_URL = "https://api.tosspayments.com/v1/payments/confirm";

    public String approvePayment(PaymentApproveRequest request) {
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);
        headers.setBasicAuth(SECRET_KEY, "");

        Map<String, Object> body = new HashMap<>();
        body.put("paymentKey", request.getPaymentKey());
        body.put("orderId", request.getOrderId());
        body.put("amount", request.getAmount());

        HttpEntity<Map<String, Object>> entity = new HttpEntity<>(body, headers);

        try {
            ResponseEntity<String> response = restTemplate.postForEntity(API_URL, entity, String.class);
//            System.out.println("결제 승인 응답: " + response.getBody());
            return response.getBody();
        } catch (HttpClientErrorException e) {
            System.err.println("HTTP 오류 발생: " + e.getStatusCode());
            System.err.println("응답 본문: " + e.getResponseBodyAsString());
            throw new RuntimeException("결제 승인 실패: " + e.getResponseBodyAsString());
        } catch (Exception e) {
            // 기타 예외 처리
            System.err.println("알 수 없는 오류 발생: " + e.getMessage());
            throw new RuntimeException("결제 승인 중 예기치 못한 오류가 발생했습니다.");
        }
    }
}

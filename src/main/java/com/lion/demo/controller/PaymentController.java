package com.lion.demo.controller;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.lion.demo.entity.PaymentApproveRequest;
import com.lion.demo.entity.TossPayment;
import com.lion.demo.service.TossPaymentService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;

import java.time.OffsetDateTime;
import java.util.Map;

@Controller
@RequestMapping("/payment")
public class PaymentController {
    @Autowired private TossPaymentService tossPaymentService;

    @GetMapping("/success")
    public String success(@RequestParam String paymentKey,
                          @RequestParam String orderId,
                          @RequestParam String amount,
                          @RequestParam Long deliveryId) {
        // 결제 승인 요청 생성
        PaymentApproveRequest request = new PaymentApproveRequest(paymentKey, orderId, Long.parseLong(amount));
//        System.out.println("===== 결제 승인 요청 데이터: " + request);
        try {
            String jsonResult = tossPaymentService.approvePayment(request);
            ObjectMapper objectMapper = new ObjectMapper();
            Map<String, Object> result = objectMapper.readValue(jsonResult, new TypeReference<Map<String, Object>>() { });
//            System.out.println("결제 승인 성공: " + result);
            TossPayment tossPayment = TossPayment.builder()
                    .id((String) result.get("orderId"))
                    .paymentKey((String) result.get("paymentKey"))
                    .name((String) result.get("orderName"))
                    .status((String) result.get("status"))
                    .approvalTime(OffsetDateTime.parse((String) result.get("approvedAt")).toLocalDateTime())
                    .paymentType(result.get("card") != null ? "card" : "other")
                    .totalPayment((int) result.get("totalAmount"))
                    .version((String) result.get("version"))
                    .build();
            tossPaymentService.insertTossPayment(tossPayment);
            return "redirect:/order/createOrder?pid=" + result.get("orderId") + "&did=" + deliveryId;
        } catch (Exception e) {
            System.out.println("결제 승인중 오류 발생: " + e.getMessage());
            return "redirect:/mall/cart";
        }
    }

    @GetMapping("/failure")
    @ResponseBody
    public String failure(@RequestParam String code, @RequestParam String message) {
        System.err.println("===== 결제 실패 코드: " + code + ", 메시지: " + message);
        return "결제 실패: " + message;
    }
}

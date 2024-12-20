package com.lion.demo.controller;

import com.lion.demo.entity.PaymentApproveRequest;
import com.lion.demo.service.TossPaymentService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;

@Controller
@RequestMapping("/payment")
public class PaymentController {
    @Autowired private TossPaymentService tossPaymentService;

    @GetMapping("/success")
    public String success(@RequestParam String paymentKey,
                          @RequestParam String orderId,
                          @RequestParam String amount) {
        // 결제 승인 요청 생성
        PaymentApproveRequest request = new PaymentApproveRequest(paymentKey, orderId, Long.parseLong(amount));
        System.out.println("===== 결제 승인 요청 데이터: " + request);
        try {
            String result = tossPaymentService.approvePayment(request);
            System.out.println("결제 승인 성공: " + result);
            return "redirect:/order/createOrder";
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

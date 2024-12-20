package com.lion.demo.entity;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class PaymentApproveRequest {
    private String paymentKey;
    private String orderId;
    private Long amount;
}

package com.lion.demo.entity;

import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Entity
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class TossPayment {
    @Id
    private String id;                      // orderId
    private String paymentKey;              // paymentKey
    private String name;                    // orderName
    private String status;                  // status
    private LocalDateTime approvalTime;     // approvedAt
    private String paymentType;             // card, virtualAccount, transfer, mobilePhone, giftCertificate
    private int totalPayment;               // totalAmount = 상품 가격 + 배송비
    private String version;                 // version
}

package com.lion.demo.repository;

import com.lion.demo.entity.TossPayment;
import org.springframework.data.jpa.repository.JpaRepository;

public interface TossPaymentRepository extends JpaRepository<TossPayment, String> {
}

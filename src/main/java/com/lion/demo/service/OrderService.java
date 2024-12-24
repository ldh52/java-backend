package com.lion.demo.service;

import com.lion.demo.entity.Cart;
import com.lion.demo.entity.DeliveryInfo;
import com.lion.demo.entity.Order;
import com.lion.demo.entity.TossPayment;

import java.time.LocalDateTime;
import java.util.List;

public interface OrderService {
    Order createOrder(String uid, List<Cart> cartList, TossPayment tossPayment, DeliveryInfo address);

    List<Order> getOrdersByUser(String uid);

    List<Order> getOrdersByDateRange(LocalDateTime start, LocalDateTime end);

    Order findById(long oid);
}

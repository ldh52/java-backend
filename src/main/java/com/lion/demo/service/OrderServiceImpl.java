package com.lion.demo.service;

import com.lion.demo.entity.*;
import com.lion.demo.repository.BookRepository;
import com.lion.demo.repository.CartRepository;
import com.lion.demo.repository.OrderRepository;
import com.lion.demo.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;

@Service
public class OrderServiceImpl implements OrderService {
    @Autowired private BookRepository bookRepository;
    @Autowired private CartRepository cartRepository;
    @Autowired private OrderRepository orderRepository;
    @Autowired private UserRepository userRepository;

    @Override
    @Transactional
    public Order createOrder(String uid, List<Cart> cartList, TossPayment tossPayment, DeliveryInfo address) {
        User user = userRepository.findById(uid).orElse(null);
        Order order = Order.builder()
                .user(user).orderDateTime(LocalDateTime.now())
                .tossPayment(tossPayment)
                .deliveryInfo(address)
                .build();

        int totalAmount = 0;
        for (Cart cart: cartList) {
            OrderItem orderItem = OrderItem.builder()
                    .order(order).book(cart.getBook()).quantity(cart.getQuantity())
                    .subPrice(cart.getBook().getPrice() * cart.getQuantity())
                    .build();
            totalAmount += orderItem.getSubPrice();
            order.addOrderItem(orderItem);
        }
        order.setTotalAmount(totalAmount);

        Order savedOrder = orderRepository.save(order);
        cartRepository.deleteAll(cartList);
        return savedOrder;
    }

    @Override
    public List<Order> getOrdersByUser(String uid) {
        return orderRepository.findByUserUidOrderByOidDesc(uid);
    }

    @Override
    public List<Order> getOrdersByDateRange(LocalDateTime start, LocalDateTime end) {
        return orderRepository.findByOrderDateTimeBetween(start, end);
    }

    @Override
    public Order findById(long oid) {
        return orderRepository.findById(oid).orElse(null);
    }
}

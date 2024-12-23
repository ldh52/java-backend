package com.lion.demo.controller;

import com.lion.demo.aspect.CheckPermission;
import com.lion.demo.aspect.LogExecutionTime;
import com.lion.demo.entity.*;
import com.lion.demo.service.*;
import jakarta.servlet.http.HttpSession;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Controller
@RequestMapping("/order")
public class OrderController {
    @Autowired private BookService bookService;
    @Autowired private CartService cartService;
    @Autowired private DeliveryAddressService deliveryAddressService;
    @Autowired private OrderService orderService;
    @Autowired private TossPaymentService tossPaymentService;
    @Autowired private UserService userService;

    @GetMapping("/createOrder")
    public String createOrder(@RequestParam String pid,
                              @RequestParam Long did, HttpSession session) {
        String uid = (String) session.getAttribute("sessUid");
        TossPayment tossPayment = tossPaymentService.findById(pid);
        DeliveryAddress deliveryAddress = deliveryAddressService.findById(did);
        List<Cart> cartList = cartService.getCartItemsByUser(uid);
        if (cartList.size() != 0) {
            Order order = orderService.createOrder(uid, cartList, tossPayment, deliveryAddress);
        }
        return "redirect:/order/list";
    }

    @GetMapping("/list")
    public String list(HttpSession session, Model model) {
        String uid = (String) session.getAttribute("sessUid");
        List<Order> orderList = orderService.getOrdersByUser(uid);
        List<String> orderTitleList = new ArrayList<>();
        for (Order order: orderList) {
            List<OrderItem> orderItems = order.getOrderItems();
            String title = orderItems.get(0).getBook().getTitle();
            int size = orderItems.size();
            if (size > 1)
                title += " 외 " + (size - 1) + " 건";
            orderTitleList.add(title);
        }
        session.setAttribute("menu", "order");
        model.addAttribute("orderList", orderList);
        model.addAttribute("orderTitleList", orderTitleList);
        return "order/list";
    }

    @GetMapping("/listAll")
    @CheckPermission("ROLE_ADMIN")
    public String listAll(HttpSession session, Model model) {
        // 2024년 12월
        LocalDateTime startTime = LocalDateTime.of(2024, 12, 1, 0, 0);
        LocalDateTime endTime = LocalDateTime.of(2024, 12, 31, 23, 59, 59, 999999999);
        List<Order> orderList = orderService.getOrdersByDateRange(startTime, endTime);
        int totalRevenue = 0, totalBooks = 0;
        List<String> orderTitleList = new ArrayList<>();
        for (Order order: orderList) {
            totalRevenue += order.getTotalAmount();
            List<OrderItem> orderItems = order.getOrderItems();
            for (OrderItem orderItem: orderItems)
                totalBooks += orderItem.getQuantity();
            String title = orderItems.get(0).getBook().getTitle();
            int size = orderItems.size();
            if (size > 1)
                title += " 외 " + (size - 1) + " 건";
            orderTitleList.add(title);
        }
        session.setAttribute("menu", "order");
        model.addAttribute("orderList", orderList);
        model.addAttribute("orderTitleList", orderTitleList);
        model.addAttribute("totalRevenue", totalRevenue);
        model.addAttribute("totalBooks", totalBooks);
        return "order/listAll";
    }

    @GetMapping("/bookStat")
    @LogExecutionTime
    public String bookStat(HttpSession session, Model model) {
        // 2024년 12월
        LocalDateTime startTime = LocalDateTime.of(2024, 12, 1, 0, 0);
        LocalDateTime endTime = LocalDateTime.of(2024, 12, 31, 23, 59, 59, 999999999);
        List<Order> orderList = orderService.getOrdersByDateRange(startTime, endTime);
        Map<Long, BookStat> map = new HashMap<>();

        for (Order order: orderList) {
            List<OrderItem> orderItems = order.getOrderItems();
            for (OrderItem item: orderItems) {
                long bid = item.getBook().getBid();
                if (map.containsKey(bid)) {
                    BookStat bookStat = map.get(bid);
                    bookStat.setQuantity(bookStat.getQuantity() + item.getQuantity());
                    map.replace(bid, bookStat);
                } else {
                    BookStat bookStat = BookStat.builder()
                            .bid(bid)
                            .title(item.getBook().getTitle())
                            .company(item.getBook().getCompany())
                            .unitPrice(item.getBook().getPrice())
                            .quantity(item.getQuantity())
                            .build();
                    map.put(bid, bookStat);
                }
            }
        }

        List<BookStat> bookStatList = new ArrayList<>();
        for (Map.Entry<Long, BookStat> entry: map.entrySet()) {
            BookStat bookStat = entry.getValue();
            bookStat.setTotalPrice(bookStat.getUnitPrice() * bookStat.getQuantity());
            bookStatList.add(bookStat);
        }
        session.setAttribute("menu", "order");
        model.addAttribute("bookStatList", bookStatList);
        return "order/bookStat";
    }

    @PostMapping("/saveDeliveryAddress")
    @ResponseBody
    public ResponseEntity<Map<String, Object>> saveDeliveryAddress(@RequestBody DeliveryAddress deliveryAddress) {
        deliveryAddress = deliveryAddressService.insertDeliveryAddress(deliveryAddress);
        Map<String, Object> response = new HashMap<>();
        response.put("message", "배송지가 저장되었습니다.");
        response.put("id", deliveryAddress.getId());
        return ResponseEntity.ok(response);
    }

}

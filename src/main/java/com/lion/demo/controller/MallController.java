package com.lion.demo.controller;

import com.lion.demo.entity.Book;
import com.lion.demo.entity.Cart;
import com.lion.demo.entity.CartDto;
import com.lion.demo.service.BookService;
import com.lion.demo.service.CartService;
import com.lion.demo.service.UserService;
import jakarta.servlet.http.HttpSession;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.domain.Page;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

@Controller
@RequestMapping("/mall")
public class MallController {
    @Autowired private CartService cartService;
    @Autowired private BookService bookService;
    @Autowired private UserService userService;
    @Value("${toss.payment.client.key}") private String TOSS_CLIENT_KEY;
    @Value("${server.ip}") private String serverIp;
    @Value("${server.port}") private String serverPort;

    @GetMapping("/list")
    public String list(@RequestParam(name="p", defaultValue = "1") int page,
                       @RequestParam(name="f", defaultValue = "title") String field,
                       @RequestParam(name="q", defaultValue = "") String query,
                       HttpSession session, Model model) {
        int cartCount = 0;
        String sessUid = (String) session.getAttribute("sessUid");
        if (sessUid != null) {
            List<Cart> cartList = cartService.getCartItemsByUser(sessUid);
            cartCount = cartList.size();
        }
        Page<Book> pagedResult = bookService.getPagedBooks(page, field, query);
        int totalPages = pagedResult.getTotalPages();
        int startPage = (int) Math.ceil((page - 0.5) / BookService.PAGE_SIZE - 1) * BookService.PAGE_SIZE + 1;
        int endPage = Math.min(startPage + BookService.PAGE_SIZE - 1, totalPages);
        List<Integer> pageList = new ArrayList<>();
        for (int i = startPage; i <= endPage; i++)
            pageList.add(i);

        session.setAttribute("menu", "mall");
        session.setAttribute("currentMallPage", page);
        model.addAttribute("bookList", pagedResult.getContent());
        model.addAttribute("field", field);
        model.addAttribute("query", query);
        model.addAttribute("totalPages", totalPages);
        model.addAttribute("startPage", startPage);
        model.addAttribute("endPage", endPage);
        model.addAttribute("pageList", pageList);
        model.addAttribute("cartCount", cartCount);
        return "mall/list";
    }

    @GetMapping("/detail/{bid}")
    public String detail(@PathVariable long bid, Model model) {
        Book book = bookService.findByBid(bid);
        model.addAttribute("book", book);
        return "mall/detail";
    }

    @PostMapping("/addItemToCart")      // /mall/detail 화면에서 카트에 담기를 눌렀을 때
    public String addItemToCart(long bid, int quantity, HttpSession session) {
        String uid = (String) session.getAttribute("sessUid");
        if (quantity > 0)
            cartService.addToCart(uid, bid, quantity);
        return "redirect:/mall/list";
    }

    @ResponseBody
    @PostMapping("/addToCart")      // /mall/list 화면에서 카트에 담기를 눌렀을 때
    public String addToCart(String bid, HttpSession session) {
        String uid = (String) session.getAttribute("sessUid");
        cartService.addToCart(uid, Long.parseLong(bid), 1);
        List<Cart> cartList = cartService.getCartItemsByUser(uid);
        int cartCount = cartList.size();
        return "" + cartCount;
    }

    @GetMapping("/cart")
    public String cart(HttpSession session, Model model) {
        String uid = (String) session.getAttribute("sessUid");
        List<Cart> cartList = cartService.getCartItemsByUser(uid);
        int totalPrice = 0;
        List<CartDto> cartDtoList = new ArrayList<>();
        for (Cart cart: cartList) {
            int subTotal = cart.getBook().getPrice() * cart.getQuantity();
            totalPrice += subTotal;
            CartDto cartDto = CartDto.builder()
                    .cid(cart.getCid())
                    .title(cart.getBook().getTitle())
                    .imageUrl(cart.getBook().getImageUrl())
                    .price(cart.getBook().getPrice())
                    .quantity(cart.getQuantity())
                    .subTotal(subTotal)
                    .build();
            cartDtoList.add(cartDto);
        }
        int deliveryCost = getDeliveryCost(totalPrice);
        int totalPayment = totalPrice + deliveryCost;

        session.setAttribute("serverIp", serverIp);
        session.setAttribute("serverPort", serverPort);
        model.addAttribute("cartList", cartList);
        model.addAttribute("cartDtoList", cartDtoList);
        model.addAttribute("totalPrice", totalPrice);
        model.addAttribute("deliveryCost", deliveryCost);
        model.addAttribute("totalPayment", totalPayment);
        model.addAttribute("TOSS_CLIENT_KEY", TOSS_CLIENT_KEY);
        return "mall/cart";
    }

    @ResponseBody
    @PostMapping("/updateCart")
    public Map<String, Object> updateCart(@RequestBody Map<String, Object> payload, HttpSession session) {
        String uid = (String) session.getAttribute("sessUid");
        long cid = Long.parseLong(payload.get("cid").toString());
        int quantity = Integer.parseInt(payload.get("quantity").toString());
//        System.out.println("======updateCart(): cid=" + cid + ", quantity=" + quantity);

        int subTotal = 0;
        if (quantity == 0) {
            cartService.removeFromCart(cid);
        } else {
            Cart cart = cartService.findById(cid);
            cart.setQuantity(quantity);
            cartService.updateCart(cart);
            subTotal = cart.getBook().getPrice() * quantity;
        }

        List<Cart> cartList = cartService.getCartItemsByUser(uid);
        int totalPrice = 0;
        for (Cart cart: cartList) {
            totalPrice += cart.getBook().getPrice() * cart.getQuantity();
        }
        int deliveryCost = getDeliveryCost(totalPrice);
        int totalPayment = totalPrice + deliveryCost;

        return Map.of(
            "success", true,
            "subTotal", subTotal,
            "totalPrice", totalPrice,
            "deliveryCost", deliveryCost,
            "totalPayment", totalPayment
        );
    }

    private int getDeliveryCost(int totalPrice) {
        return totalPrice >= 60000 ? 0 : 3000;          // 60,000원 이상이면 배송비 무료
    }
}

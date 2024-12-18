package com.lion.demo.controller;

import com.lion.demo.entity.Book;
import com.lion.demo.entity.Cart;
import com.lion.demo.service.BookService;
import com.lion.demo.service.CartService;
import com.lion.demo.service.UserService;
import jakarta.servlet.http.HttpSession;
import org.springframework.beans.factory.annotation.Autowired;
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

    @PostMapping("/addItemToCart")
    public String addItemToCart(long bid, int quantity, HttpSession session) {
        String uid = (String) session.getAttribute("sessUid");
        if (quantity > 0)
            cartService.addToCart(uid, bid, quantity);
        return "redirect:/mall/list";
    }

    @PostMapping("/addToCart")
    public String addToCart(@RequestBody Map<String, Object> requestData, HttpSession session) {
        String uid = (String) session.getAttribute("sessUid");
        long bid = Long.parseLong(requestData.get("bid").toString());
        int quantity = Integer.parseInt(requestData.get("quantity").toString());
        if (quantity > 0)
            cartService.addToCart(uid, bid, quantity);
        return "redirect:/mall/list";
    }

    @GetMapping("/cart")
    public String cart(HttpSession session, Model model) {
        String uid = (String) session.getAttribute("sessUid");
        List<Cart> cartList = cartService.getCartItemsByUser(uid);
        int totalPrice = 0;
        for (Cart cart: cartList) {
            totalPrice += cart.getBook().getPrice() * cart.getQuantity();
        }
        model.addAttribute("cartList", cartList);
        model.addAttribute("totalPrice", totalPrice);
        return "mall/cart";
    }
}

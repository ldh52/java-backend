package com.lion.demo.controller;

import com.lion.demo.entity.Book;
import com.lion.demo.service.BookService;
import com.lion.demo.service.CartService;
import com.lion.demo.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;

import java.util.List;

@Controller
@RequestMapping("/mall")
public class MallController {
    @Autowired private CartService cartService;
    @Autowired private BookService bookService;
    @Autowired private UserService userService;

    @GetMapping("/list")
    public String list(Model model) {
        List<Book> bookList = bookService.getBooksByPage(3);
        model.addAttribute("bookList", bookList);
        return "mall/list";
    }
}

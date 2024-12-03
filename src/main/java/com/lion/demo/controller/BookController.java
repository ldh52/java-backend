package com.lion.demo.controller;

import com.lion.demo.entity.Book;
import com.lion.demo.service.BookService;
import com.lion.demo.service.CsvFileReaderService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;

import java.util.List;

@Controller
@RequestMapping("/book")
public class BookController {
    @Autowired private CsvFileReaderService csvFileReaderService;
    @Autowired private BookService bookService;

    @GetMapping("/list")
    public String list(Model model) {
        List<Book> bookList = bookService.getBooksByPage(1);
        model.addAttribute("bookList", bookList);
        return "book/list";
    }

    @GetMapping("/yes24")
    public String yes24() {
        csvFileReaderService.csvFileToH2();
        return "redirect:/book/list";
    }

}

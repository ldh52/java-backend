package com.lion.demo.service;

import com.lion.demo.entity.BookEs;
import com.lion.demo.repository.BookEsRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.elasticsearch.client.elc.ElasticsearchTemplate;
import org.springframework.data.elasticsearch.client.elc.NativeQuery;
import org.springframework.data.elasticsearch.core.SearchHit;
import org.springframework.data.elasticsearch.core.SearchHits;
import org.springframework.data.elasticsearch.core.query.Query;
import org.springframework.data.elasticsearch.core.query.StringQuery;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class BookEsService {
    public static final int PAGE_SIZE = 10;
    @Autowired private BookEsRepository bookEsRepository;

    public BookEs findById(String bookId) {
        return bookEsRepository.findById(bookId).orElse(null);
    }

    public Page<BookEs> getPagedBooks(int page, String field, String query) {
        Pageable pageable = PageRequest.of(page - 1, PAGE_SIZE);
        if (field.equals("title"))
            return bookEsRepository.findByTitleContaining(query, pageable);
        if (field.equals("author"))
            return bookEsRepository.findByAuthorContaining(query, pageable);
        if (field.equals("company"))
            return bookEsRepository.findByCompanyContaining(query, pageable);
        return bookEsRepository.findBySummaryContaining(query, pageable);
    }

    public void insertBookEs(BookEs bookEs) {
        bookEsRepository.save(bookEs);
    }
}

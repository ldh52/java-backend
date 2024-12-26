package com.lion.demo.service;

import com.lion.demo.entity.BookEs;
import com.lion.demo.entity.BookEsDto;
import com.lion.demo.repository.BookEsRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.*;
import org.springframework.data.elasticsearch.client.elc.ElasticsearchTemplate;
import org.springframework.data.elasticsearch.client.elc.NativeQuery;
import org.springframework.data.elasticsearch.core.SearchHits;
import org.springframework.data.elasticsearch.core.query.Query;
import org.springframework.data.elasticsearch.core.query.StringQuery;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class BookEsService {
    public static final int PAGE_SIZE = 10;
    @Autowired private BookEsRepository bookEsRepository;
    @Autowired private ElasticsearchTemplate elasticsearchTemplate;

    public BookEs findById(String bookId) {
        return bookEsRepository.findById(bookId).orElse(null);
    }

    public void insertBookEs(BookEs bookEs) {
        bookEsRepository.save(bookEs);
    }

    public void updateBookEs(BookEs bookEs) {
        bookEsRepository.save(bookEs);
    }

    public void deleteBookEs(String bookId) {
        bookEsRepository.deleteById(bookId);
    }

    public Page<BookEsDto> getPagedBooks(int page, String field, String keyword, String sortField, String sortDirection) {
        Pageable pageable = PageRequest.of(page - 1, PAGE_SIZE);
        // 정렬 필드에 keyword 서브 필드 사용
        String sortFieldToUse = sortField + ".keyword";
        Sort.Direction direction = sortDirection.equalsIgnoreCase("asc") ? Sort.Direction.ASC : Sort.Direction.DESC;
        Query query = NativeQuery.builder()
                .withQuery(buildMatchQuery(field, keyword))
                .withSort(Sort.by(Sort.Order.desc("_score")))       // 1. matchScore 기준 정렬
                .withSort(Sort.by(direction, sortFieldToUse))     // 2. titel/author.keyword 기준 정렬
                .withTrackScores(true)
                .withPageable(pageable)
                .build();
        SearchHits<BookEs> searchHits = elasticsearchTemplate.search(query, BookEs.class);
        List<BookEsDto> bookEsDtoList = searchHits
                .getSearchHits()
                .stream()
                .map(hit -> new BookEsDto(hit.getContent(), hit.getScore()))
                .toList();

        long totalHits = searchHits.getTotalHits();
        return new PageImpl<>(bookEsDtoList, pageable, totalHits);
    }

    private Query buildMatchQuery(String field, String keyword) {
        if (keyword.isEmpty()) {
            return new StringQuery("{\"match_all\": {}}");
        }
        String queryString = String.format("""
                        {
                            "match": {
                                "%s": {
                                    "query": "%s",
                                    "fuzziness": "AUTO"
                                }
                            }
                        }        
                """,
                field, keyword
        );
        return new StringQuery(queryString);
    }
}

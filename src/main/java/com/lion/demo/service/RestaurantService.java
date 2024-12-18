package com.lion.demo.service;

import com.lion.demo.entity.Restaurant;
import com.lion.demo.entity.RestaurantDto;
import com.lion.demo.repository.RestaurantRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.*;
import org.springframework.data.elasticsearch.client.elc.ElasticsearchTemplate;
import org.springframework.data.elasticsearch.client.elc.NativeQuery;
import org.springframework.data.elasticsearch.client.elc.NativeQueryBuilder;
import org.springframework.data.elasticsearch.core.SearchHits;
import org.springframework.data.elasticsearch.core.query.Query;
import org.springframework.data.elasticsearch.core.query.StringQuery;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Map;

@Service
public class RestaurantService {
    public static final int PAGE_SIZE = 10;
    @Autowired private RestaurantRepository restaurantRepository;
    @Autowired private ElasticsearchTemplate elasticsearchTemplate;

    public Restaurant findById(String id) {
        return restaurantRepository.findById(id).orElse(null);
    }

    public void insertRestaurant(Restaurant restaurant) {
        restaurantRepository.save(restaurant);
    }

    public void deleteRestaurant(String id) {
        restaurantRepository.deleteById(id);
    }

    public Page<RestaurantDto> getPagedRestaurants(int page, String field, String keyword, String sortDirection,
                                                   boolean sortWithinResults) {
        Pageable pageable = PageRequest.of(page - 1, PAGE_SIZE);
        Sort.Direction direction = sortDirection.equalsIgnoreCase("asc") ? Sort.Direction.ASC : Sort.Direction.DESC;
        NativeQueryBuilder queryBuilder = NativeQuery.builder()
                .withQuery(buildMatchQuery(field, keyword))
                .withPageable(pageable);
        if (sortWithinResults || keyword.isEmpty()) {        // 결과내 정렬 체크 or 키워드가 비어있을 경우
            queryBuilder
                    .withSort(Sort.by(direction, "name.keyword"))       // name.keyword 기준 정렬
                    .withTrackScores(true);
        } else {                        // 결과내 정렬 체크 안됨
            queryBuilder.withSort(Sort.by(Sort.Order.desc("_score")));    // matchScore 기준 정렬
        }
        Query query = queryBuilder.build();

        SearchHits<Restaurant> searchHits = elasticsearchTemplate.search(query, Restaurant.class);
        List<RestaurantDto> restaurantDtoList = searchHits
                .getSearchHits()
                .stream()
                .map(hit -> {
                    Restaurant restaurant = hit.getContent();
                    double sunOfReviewScore = 0.0;
                    for (Map<String, Object> map: restaurant.getReviews()) {
                        sunOfReviewScore += Integer.parseInt((String) map.get("score"));
                    }
                    return RestaurantDto.builder()
                            .restaurant(restaurant)
                            .infoCount(restaurant.getInfo().size())
                            .reviewCount(restaurant.getReviews().size())
                            .reviewScore(restaurant.getReviews().size() == 0 ? 0.0 : sunOfReviewScore/restaurant.getReviews().size())
                            .matchScore(hit.getScore())
                            .build();
                })
                .toList();

        long totalHits = searchHits.getTotalHits();
        return new PageImpl<>(restaurantDtoList, pageable, totalHits);
    }

    private Query buildMatchQuery(String field, String keyword) {
        String queryString = null;
        if (keyword.isEmpty()) {
            return new StringQuery("{\"match_all\": {}}");
        }
        if (field.equals("info")) {
            // multi-line string, text block - Java 15부터 가능
            queryString = String.format("""
                        {
                            "multi_match": {
                                "query": "%s",
                                "fields": [
                                    "info.*"
                                ],
                                "fuzziness": "AUTO"
                            }
                        }        
                    """, keyword
            );
        } else if (field.equals("reviews")) {
            queryString = String.format("""
                        {
                            "nested": {
                                "path": "reviews",
                                "query": {
                                    "match": {
                                        "reviews.review": {
                                            "query": "%s",
                                            "fuzziness": "AUTO"
                                        }
                                    }
                                }
                            }
                        }        
                    """, keyword
            );
        } else {
            queryString = String.format("""
                    {
                        "match": {
                            "%s": {
                                "query": "%s",
                                "fuzziness": "AUTO"
                            }
                        }
                    }        
                """, field, keyword
            );
        }
        return new StringQuery(queryString);
    }
}

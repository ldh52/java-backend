package com.lion.demo.entity;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class CartDto {
    private long cid;
    private String uid;
    private long bid;
    private String title;
    private String imageUrl;
    private int price;
    private int quantity;
    private int subTotal;
}

package com.lion.demo.service;

import com.lion.demo.entity.Cart;

import java.util.List;

public interface CartService {
    Cart findById(long cid);

    List<Cart> getCartItemsByUser(String uid);

    void addToCart(String uid, long bid, int quantity);

    void updateCart(Cart cart);

    void removeFromCart(long cid);

    void clearCart(String uid);
}

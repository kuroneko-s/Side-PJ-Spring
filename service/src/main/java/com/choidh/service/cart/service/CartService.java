package com.choidh.service.cart.service;

import com.choidh.service.cart.entity.Cart;
import com.choidh.service.learning.entity.Learning;

import java.util.List;

public interface CartService {
    void addCart(Long accountId, Long learningId);

    void deleteCart(Cart cart, List<Learning> learningList);
}

package com.choidh.service.cart.service;

import com.choidh.service.cart.entity.Cart;
import com.choidh.service.learning.entity.Learning;

import java.util.List;

public interface CartService {
    /**
     * 카트 생성
     */
    Cart regCart(Long accountId);

    /**
     * 카트 조회 By Account Id
     */
    Cart getCart(Long accountId);

    /**
     * 유저의 카트에 강의 추가.
     */
    void addCart(Long accountId, Long learningId);

    /**
     * 카트에서 강의 삭제
     */
    void deleteCart(Cart cart, List<Learning> learningList);
}

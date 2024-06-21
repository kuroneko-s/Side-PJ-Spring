package com.choidh.service.cart.service;

import com.choidh.service.cart.entity.Cart;
import com.choidh.service.cart.vo.BuyVO;
import com.choidh.service.learning.entity.Learning;

import javax.annotation.Nullable;
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
    String addCart(Long accountId, Long learningId);

    /**
     * 카트에서 강의 삭제
     */
    void deleteCart(Cart cart, List<Learning> learningList);

    /**
     * 구매 화면 View
     */
    BuyVO buyCartView(Long accountId, @Nullable Long learningId);
}

package com.choidh.web.cart.controller;


import com.choidh.service.account.entity.Account;
import com.choidh.web.cart.service.CartService;
import com.choidh.web.common.annotation.CurrentAccount;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;

@Slf4j
@Controller
@RequestMapping("/cart")
@RequiredArgsConstructor
public class CartController {
    private final CartService cartService;

    /**
     * Get 장바구니 화면 이동
     */
    @GetMapping("/learning")
    public String getCartLearningView(@CurrentAccount Account account, Model model) {
        cartService.getCartLearning(account.getId(), model);

        return "profile/cart";
    }
}

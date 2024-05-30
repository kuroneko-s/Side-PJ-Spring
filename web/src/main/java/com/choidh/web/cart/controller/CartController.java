package com.choidh.web.cart.controller;


import com.choidh.service.account.entity.Account;
import com.choidh.service.account.service.AccountService;
import com.choidh.service.joinTables.entity.LearningCartJoinTable;
import com.choidh.service.learning.entity.Learning;
import com.choidh.web.common.annotation.CurrentAccount;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;

import java.util.List;
import java.util.stream.Collectors;

@Slf4j
@Controller
@RequestMapping("/cart")
@RequiredArgsConstructor
public class CartController {
    private final AccountService accountService;

    /**
     * Get 장바구니 화면 이동
     */
    @GetMapping("/learning")
    public String getCartLearningView(@CurrentAccount Account account, Model model) {
        account = accountService.getAccountByIdWithCart(account.getId());
        List<Learning> learningList = account.getCart().getLearningCartJoinTables().stream().map(LearningCartJoinTable::getLearning).collect(Collectors.toList());

        model.addAttribute("account", account);
        model.addAttribute("cartList", learningList);
        model.addAttribute("totalPrice", learningList.stream().mapToInt(Learning::getPrice).sum());

        return "profile/cart";
    }
}

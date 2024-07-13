package com.choidh.web.cart.controller;


import com.choidh.service.account.entity.Account;
import com.choidh.service.account.service.AccountService;
import com.choidh.service.cart.service.CartService;
import com.choidh.service.cart.vo.BuyVO;
import com.choidh.web.common.annotation.CurrentAccount;
import com.choidh.web.kakao.vo.KakaoPayForm;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import static com.choidh.service.common.vo.AppConstant.getTitle;

@Slf4j
@Controller
@RequestMapping("/cart")
@RequiredArgsConstructor
public class CartController {
    private final CartService cartService;

    /**
     * GET 장바구니 View
     */
    @GetMapping("/buy")
    public String getBuyView(@CurrentAccount Account account, Model model, @RequestParam(name = "learningId", required = false) Long learningId) {
        BuyVO buyVO = cartService.buyCartView(account.getId(), learningId);

        model.addAttribute("learningList", buyVO.getLearningList());
        model.addAttribute("totalPrice", buyVO.getTotalPrice());
        model.addAttribute("learningImageMap", buyVO.getLearningImageMap());

        model.addAttribute(new KakaoPayForm());
        model.addAttribute("pageTitle", getTitle("장바구니"));

        return "cart/list/index";
    }

    /**
     * 카트에 강의 추가. API
     */
    @PostMapping("/append")
    @ResponseBody
    public ResponseEntity cartLearning(@CurrentAccount Account account, @RequestBody Long learningId) {
        try {
            String resultCode = cartService.addCart(account.getId(), learningId);

            return ResponseEntity.ok(resultCode);
        } catch (Exception e) {
            log.error(e.getMessage(), e);

            return ResponseEntity.badRequest().body(e.getMessage());
        }
    }
}

package com.choidh.web.cart.controller;


import com.choidh.service.account.entity.Account;
import com.choidh.service.account.service.AccountService;
import com.choidh.service.cart.service.CartService;
import com.choidh.service.cart.vo.BuyVO;
import com.choidh.service.joinTables.entity.LearningCartJoinTable;
import com.choidh.service.learning.entity.Learning;
import com.choidh.web.common.annotation.CurrentAccount;
import com.choidh.web.kakao.vo.KakaoPayForm;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.stream.Collectors;

import static com.choidh.service.common.AppConstant.getTitle;

@Slf4j
@Controller
@RequestMapping("/cart")
@RequiredArgsConstructor
public class CartController {
    private final AccountService accountService;
    private final CartService cartService;

    /**
     * Get 장바구니 화면 이동
     */
    @GetMapping("/learning")
    public String getCartLearningView(@CurrentAccount Account account, Model model) {
        account = accountService.getAccountByIdWithLearningInCart(account.getId());
        List<Learning> learningList = account.getCart().getLearningCartJoinTables().stream().map(LearningCartJoinTable::getLearning).collect(Collectors.toList());

        model.addAttribute("account", account);
        model.addAttribute("cartList", learningList);
        model.addAttribute("totalPrice", learningList.stream().mapToInt(Learning::getPrice).sum());

        return "profile/cart";
    }

    /**
     * 카트에 강의 추가. API
     */
    @PostMapping("/append")
    @ResponseBody
    public ResponseEntity cartLearning(@CurrentAccount Account account,@RequestBody Long learningId) {
        try {
            String resultCode = cartService.addCart(account.getId(), learningId);

            return ResponseEntity.ok(resultCode);
        } catch (Exception e) {
            log.error(e.getMessage(), e);

            return ResponseEntity.badRequest().body(e.getMessage());
        }
    }

    /**
     * GET 구매 화면 View
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
}

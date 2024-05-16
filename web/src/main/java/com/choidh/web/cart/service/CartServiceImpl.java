package com.choidh.web.cart.service;

import com.choidh.service.account.entity.Account;
import com.choidh.service.account.repository.AccountRepository;
import com.choidh.service.learning.entity.Learning;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.ui.Model;

import java.util.Set;

import static com.choidh.service.common.AppConstant.getAccountNotFoundErrorMessage;

@Slf4j
@Service
@RequiredArgsConstructor
public class CartServiceImpl implements CartService {
    private final AccountRepository accountRepository;

    @Override
    public void getCartLearning(Long accountId, Model model) {
        Account newAccount = accountRepository.findById(accountId)
                .orElseThrow(() -> new IllegalArgumentException(getAccountNotFoundErrorMessage(accountId)));
        Set<Learning> cartList = newAccount.getCartList();

        model.addAttribute("account", newAccount);
        model.addAttribute("cartList", cartList);
        model.addAttribute("totalPrice", cartList.stream().mapToInt(Learning::getPrice).sum());
    }
}

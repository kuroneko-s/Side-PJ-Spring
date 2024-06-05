package com.choidh.service.cart.service;

import com.choidh.service.account.entity.Account;
import com.choidh.service.account.service.AccountService;
import com.choidh.service.cart.entity.Cart;
import com.choidh.service.cart.repository.CartRepository;
import com.choidh.service.joinTables.entity.LearningCartJoinTable;
import com.choidh.service.joinTables.service.LearningCartService;
import com.choidh.service.learning.entity.Learning;
import com.choidh.service.learning.service.LearningService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.HashSet;
import java.util.List;

@Slf4j
@Service
@Transactional(readOnly = true)
public class CartServiceImpl implements CartService {
    @Autowired private CartRepository cartRepository;
    @Autowired private LearningCartService learningCartService;
    @Autowired private AccountService accountService;
    @Autowired private LearningService learningService;

    /**
     * 카트 생성
     */
    @Override
    @Transactional
    public Cart regCart(Long accountId) {
        Account account = accountService.getAccountById(accountId);
        Cart cart = cartRepository.save(Cart.builder()
                .account(account)
                .learningCartJoinTables(new HashSet<>())
                .build());
        account.setCart(cart);
        return cart;
    }

    @Override
    @Transactional
    public void addCart(Long accountId, Long learningId) {
        Account account = accountService.getAccountByIdWithLearningInCart(accountId);
        Learning learning = learningService.getLearningById(learningId);

        LearningCartJoinTable learningCartJoinTable = new LearningCartJoinTable();
        learningCartJoinTable.setLearning(learning);

        account.getCart().addLearningCartJoinTable(learningCartJoinTable);

        learningCartService.saveLearningCart(learningCartJoinTable);
    }

    @Override
    @Transactional
    public void deleteCart(Cart cart, List<Learning> learningList) {
        for (Learning learning : learningList) {
            int result = learningCartService.removeLearning(cart.getId(), learning.getId());

            if (result != 1)
                throw new IllegalArgumentException("삭제 실패. cart id - " + cart.getId() + ", learning id - " + learning.getId());
        }
    }
}

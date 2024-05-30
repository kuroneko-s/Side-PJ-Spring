package com.choidh.service.cart.service;

import com.choidh.service.account.entity.Account;
import com.choidh.service.account.service.AccountService;
import com.choidh.service.cart.entity.Cart;
import com.choidh.service.cart.repository.CartRepository;
import com.choidh.service.joinTables.entity.LearningCartJoinTable;
import com.choidh.service.joinTables.service.LearningCartService;
import com.choidh.service.learning.entity.Learning;
import com.choidh.service.learning.service.LearningService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Slf4j
@Service
@Transactional(readOnly = true)
@RequiredArgsConstructor
public class CartServiceImpl implements CartService {
    private final CartRepository cartRepository;
    private final LearningCartService learningCartService;
    private final AccountService accountService;
    private final LearningService learningService;

    @Override
    @Transactional
    public void addCart(Long accountId, Long learningId) {
        Account account = accountService.getAccountById(accountId);
        Learning learning = learningService.getLearningById(learningId);

        LearningCartJoinTable learningCartJoinTable = new LearningCartJoinTable();
        learningCartJoinTable.setLearning(learning);

        Cart cart = new Cart();
        cart.setAccount(account);
        cart.addLearningCartJoinTable(learningCartJoinTable);

        cartRepository.save(cart);
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

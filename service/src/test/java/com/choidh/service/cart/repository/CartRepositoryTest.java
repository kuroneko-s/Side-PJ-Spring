package com.choidh.service.cart.repository;

import com.choidh.service.AbstractRepositoryTestConfig;
import com.choidh.service.account.entity.Account;
import com.choidh.service.cart.entity.Cart;
import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.annotation.Rollback;

@Slf4j
@Rollback(value = false)
class CartRepositoryTest extends AbstractRepositoryTestConfig {
    @Autowired
    private CartRepository cartRepository;

    @Test
    @DisplayName("카트 조회. By Account Id With Learning")
    public void findByAccountIdWithLearningCartJoinTables() throws Exception {
        Account testAccount = createAccount();

        Account account = createAccount("test2", "test2@test.com");

        Cart cart = cartRepository.findByAccountIdWithLearningCartJoinTables(account.getId());
    }

}
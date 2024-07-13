package com.choidh.web.cart.controller;

import com.choidh.service.account.entity.Account;
import com.choidh.service.account.vo.AccountType;
import com.choidh.service.joinTables.entity.LearningCartJoinTable;
import com.choidh.service.learning.entity.Learning;
import com.choidh.web.AbstractControllerTestConfig;
import com.choidh.web.config.WithAccount;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.http.MediaType;

import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

import static org.junit.jupiter.api.Assertions.*;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.csrf;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@Slf4j
@RequiredArgsConstructor
class CartControllerTest extends AbstractControllerTestConfig {

    @Test
    @DisplayName("장바구니 화면 접근 (비로그인)")
    public void getCartViewNoneLogin() throws Exception {
        mockMvc.perform(get("/cart/buy"))
                .andExpect(status().is3xxRedirection());
    }

    @Test
    @WithAccount("test@test.com")
    @DisplayName("장바구니 화면 접근 (로그인)")
    public void getCartViewLogin() throws Exception {
        mockMvc.perform(get("/cart/buy"))
                .andExpect(model().attributeExists("learningList"))
                .andExpect(model().attributeExists("totalPrice"))
                .andExpect(model().attributeExists("learningImageMap"))
                .andExpect(model().attributeExists("account"))
                .andExpect(model().attributeExists("kakaoPayForm"))
                .andExpect(model().attributeExists("pageTitle"))
                .andExpect(status().isOk())
                .andExpect(view().name("cart/list/index"));
    }

    @Test
    @DisplayName("장바구니 추가 동작 (비로그인)")
    public void getAddCartNoneLogin() throws Exception {
        mockMvc.perform(post("/cart/append")
                        .param("learningId", "1")
                        .with(csrf()))
                .andExpect(status().is3xxRedirection());
    }

    @Test
    @WithAccount("test@test.com")
    @DisplayName("장바구니 추가 동작 (로그인)")
    public void getAddCartLogin() throws Exception {
        Account account = accountService.getAccountByEmail("test@test.com");

        Account before = accountService.getAccountByIdWithLearningInCart(account.getId());
        List<Learning> learningList = learningService.getTop12LearningListByOpeningDate();
        Learning learning = learningList.get(0);

        List<Long> beforeList = before.getCart().getLearningCartJoinTables().stream().map(learningCartJoinTable -> learningCartJoinTable.getLearning().getId()).collect(Collectors.toList());
        assertFalse(beforeList.contains(learning.getId()));

        this.persistClear();
        mockMvc.perform(post("/cart/append")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(String.valueOf(learning.getId()))
                        .with(csrf()))
                .andExpect(status().isOk());
        this.persistClear();

        Account afterAccount = accountService.getAccountByIdWithLearningInCart(account.getId());
        List<Long> afterList = afterAccount.getCart().getLearningCartJoinTables().stream().map(learningCartJoinTable -> learningCartJoinTable.getLearning().getId()).collect(Collectors.toList());
        assertTrue(afterList.contains(learning.getId()));
    }
}
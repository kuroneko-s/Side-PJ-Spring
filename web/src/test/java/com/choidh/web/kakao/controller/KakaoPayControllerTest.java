package com.choidh.web.kakao.controller;

import com.choidh.service.account.entity.Account;
import com.choidh.service.account.service.AccountService;
import com.choidh.service.event.entity.Event;
import com.choidh.service.kakao.service.KakaoPayService;
import com.choidh.service.kakao.vo.KakaoPayReadyVO;
import com.choidh.service.learning.entity.Learning;
import com.choidh.service.learning.service.LearningService;
import com.choidh.web.AbstractControllerTestConfig;
import com.choidh.web.config.WithAccount;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.Iterator;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import static org.junit.jupiter.api.Assertions.*;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.csrf;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@Slf4j
@RequiredArgsConstructor
class KakaoPayControllerTest extends AbstractControllerTestConfig {
    @Autowired
    private LearningService learningService;
    @Autowired
    private AccountService accountService;
    @Autowired
    private KakaoPayService kakaoPayService;

    @Test
    @DisplayName("카카오 페이 결제 요청 (비로그인)")
    public void postKakaopayPaymentRequestNoneLogin() throws Exception {
        List<Learning> learningList = learningService.getTop12LearningListByOpeningDate();
        String[] idList = learningList.stream().map(learning -> learning.getId().toString()).toArray(String[]::new);

        mockMvc.perform(post("/kakaoPay")
                        .param("learningIds", String.join(",", idList))
                        .with(csrf()))
                .andExpect(status().is3xxRedirection());
    }

    @Test
    @WithAccount("test@test.com")
    @DisplayName("카카오 페이 결제 요청 (로그인)")
    public void postKakaopayPaymentRequestLogin() throws Exception {
        Account account = accountService.getAccountByEmail("test@test.com");

        List<Learning> learningList = learningService.getTop12LearningListByOpeningDate();
        String[] idList = learningList.stream().map(learning -> learning.getId().toString()).toArray(String[]::new);

        KakaoPayReadyVO kakaoPayReadyVO = kakaoPayService.kakaoPayReady();

        mockMvc.perform(post("/kakaoPay")
                        .param("learningIds", String.join(",", idList))
                        .with(csrf()))
                .andExpect(request().sessionAttribute(account.getId() + "_learning_id", String.join(",", idList)))
                .andExpect(status().is3xxRedirection());
    }
}
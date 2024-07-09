package com.choidh.web.mail.controller;

import com.choidh.service.account.entity.Account;
import com.choidh.service.account.vo.RegAccountVO;
import com.choidh.service.mail.service.EmailService;
import com.choidh.service.mail.vo.EmailMessageVO;
import com.choidh.web.AbstractControllerTestConfig;
import com.choidh.web.account.vo.AccountVO;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.test.web.servlet.MockMvc;

import java.time.LocalDateTime;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.csrf;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@Slf4j
@RequiredArgsConstructor
class EmailControllerTest extends AbstractControllerTestConfig {
    @Autowired
    private MockMvc mockMvc;
    @MockBean
    private EmailService emailService;

    @Test
    @DisplayName("로그인 메일 인증 - 성공")
    public void getEmailAuthentication_success() throws Exception {
        AccountVO accountVO = new AccountVO();
        accountVO.setEmail("test@test.com");
        accountVO.setPassword("1234567890");
        accountVO.setPasswordcheck("1234567890");
        accountVO.setNickname("Test");
        Account beforeAccount = modelMapper.map(accountVO, Account.class);
        Account account = accountService.regAccount(modelMapper.map(beforeAccount, RegAccountVO.class));
        verify(emailService, times(1)).sendEmail(EmailMessageVO.getInstanceForAccount(beforeAccount)); // 메서드 호출 여부 검증

        mockMvc.perform(
                        get("/mailAuth")
                                .param("token", account.getTokenForEmailForAuthentication())
                                .param("email", account.getEmail())
                )
                .andExpect(flash().attributeExists("account"))
                .andExpect(flash().attributeExists("success"))
                .andExpect(redirectedUrl("/"))
                .andExpect(status().is3xxRedirection());
        verify(emailService, times(1)).sendEmail(EmailMessageVO.getInstanceForAccount(account)); // 메서드 호출 여부 검증

        Account emailAccount = accountService.getAccountByEmailAndChecked("test@test.com", true);
        assertNotNull(emailAccount);
    }

    @Test
    @DisplayName("로그인 메일 인증 - 실패(token Error")
    public void getEmailToken_fail_authentecated() throws Exception {
        AccountVO accountVO = new AccountVO();
        accountVO.setEmail("test@test.com");
        accountVO.setPassword("1234567890");
        accountVO.setPasswordcheck("1234567890");
        accountVO.setNickname("Test");
        Account account = accountService.regAccount(modelMapper.map(accountVO, RegAccountVO.class));

        mockMvc.perform(
                        get("/mailAuth")
                                .param("token", account.getTokenForEmailForAuthentication() + "dp2")
                                .param("email", account.getEmail())
                )
                .andExpect(model().attributeExists("message"))
                .andExpect(model().attributeExists("email"))
                .andExpect(model().attributeExists("emailTokenVO"))
                .andExpect(view().name("navbar/token_validation"))
                .andExpect(status().isOk());

        Account emailAccount = accountService.getAccountByEmailAndChecked("test@test.com", true);
        assertNull(emailAccount);
    }

    @Test
    @DisplayName("로그인 메일 인증 - 실패(email Error")
    public void getEmailToken_fail_email() throws Exception {
        AccountVO accountVO = new AccountVO();
        accountVO.setEmail("test@test.com");
        accountVO.setPassword("1234567890");
        accountVO.setPasswordcheck("1234567890");
        accountVO.setNickname("Test");
        Account account = accountService.regAccount(modelMapper.map(accountVO, RegAccountVO.class));

        mockMvc.perform(
                        get("/mailAuth")
                                .param("token", account.getTokenForEmailForAuthentication())
                                .param("email", "kuroneko2@naver.com")
                )
                .andExpect(model().attributeExists("message"))
                .andExpect(model().attributeExists("email"))
                .andExpect(model().attributeExists("emailTokenVO"))
                .andExpect(view().name("navbar/token_validation"))
                .andExpect(status().isOk());

        Account emailAccount = accountService.getAccountByEmailAndChecked("test@test.com", true);
        assertNull(emailAccount);
    }

    @Test
    @DisplayName("로그인 메일 재인증 - 성공")
    public void reGetEmailAuthentication_success() throws Exception {
        AccountVO accountVO = new AccountVO();
        accountVO.setEmail("test@test.com");
        accountVO.setPassword("1234567890");
        accountVO.setPasswordcheck("1234567890");
        accountVO.setNickname("Test");
        Account account = accountService.regAccount(modelMapper.map(accountVO, RegAccountVO.class));
        account.setCreateTimeOfEmailToken(LocalDateTime.now().minusHours(3));

        mockMvc.perform(
                        post("/mailAuthRetry")
                                .param("email", accountVO.getEmail())
                                .with(csrf())
                )
                .andExpect(flash().attributeExists("message"))
                .andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrl("/"));

        Account newAccount = accountService.getAccountByEmailAndChecked(accountVO.getEmail(), false);
        assertNotNull(newAccount.getTokenForEmailForAuthentication());
    }

    @Test
    @DisplayName("로그인 메일 재인증 - 실패 (Time Error)")
    public void reGetEmailAuthentication_fail() throws Exception {
        AccountVO accountVO = new AccountVO();
        accountVO.setEmail("test@test.com");
        accountVO.setPassword("1234567890");
        accountVO.setPasswordcheck("1234567890");
        accountVO.setNickname("Test");
        accountService.regAccount(modelMapper.map(accountVO, RegAccountVO.class));

        mockMvc.perform(
                        post("/mailAuthRetry")
                                .param("email", accountVO.getEmail())
                                .with(csrf())
                )
                .andExpect(model().attributeExists("message"))
                .andExpect(model().attributeExists("emailTokenVO"))
                .andExpect(view().name("navbar/token_validation"))
                .andExpect(status().isOk());

        Account account = accountService.getAccountByEmailAndChecked(accountVO.getEmail(), false);
        assertNotNull(account);
    }
}
package com.choidh.web.mail.controller;

import com.choidh.service.account.entity.Account;
import com.choidh.service.account.vo.web.RegAccountVO;
import com.choidh.web.AbstractControllerTestConfig;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.time.LocalDateTime;

import static org.junit.jupiter.api.Assertions.*;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.csrf;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@Slf4j
@RequiredArgsConstructor
class EmailControllerTest extends AbstractControllerTestConfig {

    @Test
    @DisplayName("Get 이메일 재인증 View.")
    public void getEmailReAuthenticationView() throws Exception {
        mockMvc.perform(get("/registerSuccess"))
                .andExpect(model().attributeExists("pageTitle", "pageContent"))
                .andExpect(model().attribute("pageContent", "security/register/registerAuthenticationMail"))
                .andExpect(view().name("security/index"));
    }

    @Test
    @DisplayName("Get 이메일 인증 처리 (성공)")
    public void getEmailAuthentication_Success() throws Exception {
        Account account = accountService.regAccount(RegAccountVO.builder()
                .email("test@test.com")
                .password("1234567890")
                .passwordcheck("1234567890")
                .nickname("test")
                .build());

//        EmailMessageVO emailMessageVO = EmailMessageVO.getInstanceForAccount(account);
//        verify(emailService, times(1)).sendEmail(emailMessageVO); // 메서드 호출 여부 검증

        mockMvc.perform(
                        get("/mailAuth")
                                .param("token", account.getTokenForEmailForAuthentication())
                                .param("email", account.getEmail())
                )
                .andExpect(flash().attributeExists("success"))
                .andExpect(redirectedUrl("/login"))
                .andExpect(status().is3xxRedirection());

//        verify(emailService, times(1)).sendEmail(emailMessageVO); // 메서드 호출 여부 검증

        Account emailAccount = accountService.getAccountByEmailAndChecked("test@test.com", true);
        assertNotNull(emailAccount);
    }

    @Test
    @DisplayName("Get 이메일 인증 처리 실패(TOKEN)")
    public void getEmailAuthentication_Fail_TOKEN() throws Exception {
        Account account = accountService.regAccount(RegAccountVO.builder()
                .email("test@test.com")
                .password("1234567890")
                .passwordcheck("1234567890")
                .nickname("test")
                .build());

        mockMvc.perform(
                        get("/mailAuth")
                                .param("token", account.getTokenForEmailForAuthentication() + "dp2")
                                .param("email", account.getEmail())
                )
                .andExpect(model().attributeExists("message"))
                .andExpect(model().attributeExists("email"))
                .andExpect(model().attributeExists("emailFormVO"))
                .andExpect(model().attributeExists("pageTitle"))
                .andExpect(model().attributeExists("pageContent"))
                .andExpect(model().attribute("pageContent", "security/register/registerAuthenticationMail"))
                .andExpect(view().name("security/index"))
                .andExpect(status().isOk());

        Account emailAccount = accountService.getAccountByEmailAndChecked("test@test.com", true);
        assertNull(emailAccount);
    }

    @Test
    @DisplayName("Get 이메일 인증 처리 실패(EMAIL)")
    public void getEmailAuthentication_Fail_EMAIL() throws Exception {
        Account account = accountService.regAccount(RegAccountVO.builder()
                .email("test@test.com")
                .password("1234567890")
                .passwordcheck("1234567890")
                .nickname("test")
                .build());

        mockMvc.perform(
                        get("/mailAuth")
                                .param("token", account.getTokenForEmailForAuthentication())
                                .param("email", "kuroneko2@naver.com")
                )
                .andExpect(model().attributeExists("message"))
                .andExpect(model().attributeExists("email"))
                .andExpect(model().attributeExists("emailFormVO"))
                .andExpect(model().attributeExists("pageTitle"))
                .andExpect(model().attributeExists("pageContent"))
                .andExpect(model().attribute("pageContent", "security/register/registerAuthenticationMail"))
                .andExpect(view().name("security/index"))
                .andExpect(status().isOk());

        Account emailAccount = accountService.getAccountByEmailAndChecked("test@test.com", true);
        assertNull(emailAccount);
    }

    @Test
    @DisplayName("로그인 메일 재인증 - 성공")
    public void reGetEmailAuthentication_success() throws Exception {
        Account account = accountService.regAccount(RegAccountVO.builder()
                .email("test@test.com")
                .password("1234567890")
                .passwordcheck("1234567890")
                .nickname("test")
                .build());

        mockMvc.perform(
                        post("/mailAuthRetry")
                                .param("email", account.getEmail())
                                .with(csrf())
                )
                .andExpect(model().attributeExists("message"))
                .andExpect(model().attribute("pageContent", "security/register/registerAuthenticationMail"))
                .andExpect(view().name("security/index"))
                .andExpect(status().isOk());

        this.persistClear();

        Account newAccount = accountService.getAccountByEmailAndChecked(account.getEmail(), false);
        assertNotNull(newAccount.getTokenForEmailForAuthentication());
        assertTrue(newAccount.getCreateTimeOfEmailToken().isBefore(LocalDateTime.now().plusMinutes(1)));
    }

    @Test
    @DisplayName("로그인 메일 재인증 - 실패 (Time Error)")
    public void reGetEmailAuthentication_fail() throws Exception {
        Account account = accountService.regAccount(RegAccountVO.builder()
                .email("test@test.com")
                .password("1234567890")
                .passwordcheck("1234567890")
                .nickname("test")
                .build());

        mockMvc.perform(
                        post("/mailAuthRetry")
                                .param("email", account.getEmail())
                                .with(csrf())
                )
                .andExpect(model().attributeExists("message"))
                .andExpect(model().attribute("pageContent", "security/register/registerAuthenticationMail"))
                .andExpect(view().name("security/index"))
                .andExpect(status().isOk());

        Account newAccount = accountService.getAccountByEmailAndChecked(account.getEmail(), false);
        assertNotNull(newAccount);
    }
}
package com.choidh.web.account.controller;

import com.choidh.service.account.entity.Account;
import com.choidh.web.AbstractControllerTestConfig;
import com.choidh.web.config.WithAccount;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.csrf;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@Slf4j
@RequiredArgsConstructor
class AccountControllerTest extends AbstractControllerTestConfig {
    @Test
    @DisplayName("(GET)/register - 회원가입 화면 요청 - 성공")
    public void postCreateAccountForm() throws Exception{
        mockMvc.perform(get("/register"))
                .andExpect(model().attributeExists("accountVO"))
                .andExpect(model().attributeExists("pageTitle"))
                .andExpect(model().attributeExists("pageContent"))
                .andExpect(view().name("security/index"))
                .andExpect(status().isOk());
    }

    @Test
    @DisplayName("POST 로그인 계정 생성 - 성공")
    public void postAccount_Create_success() throws Exception{
        String email = "test@test.com";

        mockMvc.perform(
                        post("/register")
                                .param("nickname", "Test")
                                .param("email", email)
                                .param("password", "1234567890")
                                .param("passwordcheck", "1234567890")
                                .with(csrf())
                )
                .andExpect(model().hasNoErrors())
                .andExpect(model().attributeExists("message"))
                .andExpect(model().attributeExists("pageTitle"))
                .andExpect(model().attributeExists("pageContent"))
                .andExpect(view().name("security/index"))
                .andExpect(status().isOk());

        Account account = accountService.getAccountByEmailAndChecked(email, false);
        assertNotNull(account);
        assertTrue(passwordEncoder.matches("1234567890", account.getPassword()));
    }

    @Test
    @DisplayName("로그인 계정 생성 - 실패 (Password Error)")
    public void postAccount_Create_fail_password() throws Exception{
        String email = "test@test.com";

        mockMvc.perform(
                        post("/register")
                                .param("email", email)
                                .param("password", "1234567890")
                                .param("passwordcheck", "12345678")
                                .param("nickname", "Test")
                                .with(csrf())
                )
                .andExpect(model().hasErrors())
                .andExpect(view().name("security/index"))
                .andExpect(status().isOk());

        Account account = accountService.getAccountByEmailAndChecked(email, false);
        assertNull(account);
    }

    @Test
    @DisplayName("로그인 계정 생성 - 실패 (Blank Value)")
    public void postAccount_Create_fail_blank() throws Exception{
        String email = "test@test.com";

        mockMvc.perform(
                        post("/register")
                                .param("email", email)
                                .param("password", "1234567890")
                                .param("passwordcheck", "12345678")
                                .with(csrf())
                )
                .andExpect(model().hasErrors())
                .andExpect(view().name("security/index"))
                .andExpect(status().isOk());

        Account account = accountService.getAccountByEmailAndChecked(email, false);
        assertNull(account);
    }

    @Test
    @WithAccount("test@test.com")
    @DisplayName("로그인 계정 생성 - 실패 (Email_Duplication Error)")
    public void postAccount_Create_fail_duplication_email() throws Exception{
        String email = "test@test.com";

        mockMvc.perform(
                        post("/register")
                                .param("email", email)
                                .param("password", "1234567890")
                                .param("passwordcheck", "1234567890")
                                .param("nickname", "test2")
                                .with(csrf())
                )
                .andExpect(model().hasErrors())
                .andExpect(view().name("security/index"))
                .andExpect(status().isOk());

        Account account = accountService.getAccountByEmailAndChecked(email, false);
        assertNull(account);
    }

    @Test
    @WithAccount("test@test.com")
    @DisplayName("로그인 계정 생성 - 실패 (Nickname_Duplication Error)")
    public void postAccount_Create_fail_duplication_nickname() throws Exception{
        String nickName = "test";

        mockMvc.perform(
                        post("/register")
                                .param("email", "test2@test.com")
                                .param("password", "1234567890")
                                .param("passwordcheck", "1234567890")
                                .param("nickname", nickName)
                                .with(csrf())
                )
                .andExpect(model().hasErrors())
                .andExpect(view().name("security/index"))
                .andExpect(status().isOk());

        Account newAccount = accountService.getAccountByEmailAndChecked("test2@test.com", false);
        assertNull(newAccount);
    }
}
package com.choidh.web.account.controller;

import com.choidh.service.account.entity.Account;
import com.choidh.service.account.repository.AccountRepository;
import com.choidh.service.account.vo.RegAccountVO;
import com.choidh.service.mail.service.EmailService;
import com.choidh.service.mail.vo.EmailForAuthenticationVO;
import com.choidh.service.account.service.AccountServiceImpl;
import com.choidh.web.account.vo.AccountVO;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.csrf;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@ActiveProfiles("local")
@Slf4j
@SpringBootTest
@AutoConfigureMockMvc
@Transactional
@RequiredArgsConstructor(onConstructor = @__(@Autowired))
class AccountControllerTest {
    private final MockMvc mockMvc;
    private final ModelMapper modelMapper;
    private final PasswordEncoder passwordEncoder;
    private final AccountRepository accountRepository;
    private final AccountServiceImpl accountServiceImpl;

    @MockBean private EmailService emailService;

    @Test
    @DisplayName("GET 로그인 화면 요청 - 성공")
    public void postCreateAccountForm() throws Exception{
        mockMvc.perform(get("/register"))
                .andExpect(model().attributeExists("accountVO"))
                .andExpect(view().name("navbar/create_account"))
                .andExpect(status().isOk());
    }

    @Test
    @DisplayName("POST 로그인 계정 생성 - 성공")
    public void postAccount_Create_success() throws Exception{
        mockMvc.perform(
                        post("/register")
                                .param("email", "test@test.com")
                                .param("password", "1234567890")
                                .param("passwordcheck", "1234567890")
                                .param("nickname", "Test")
                                .with(csrf())
                )
                .andExpect(model().hasNoErrors())
                .andExpect(model().attributeExists("emailTokenVO"))
                .andExpect(model().attributeExists("message"))
                .andExpect(view().name("navbar/token_validation"))
                .andExpect(status().isOk());

        Account account = accountRepository.findByNicknameAndChecked("Test", false);
        assertNotNull(account);
        assertTrue(passwordEncoder.matches("1234567890", account.getPassword()));
    }

    @Test
    @DisplayName("로그인 계정 생성 - 실패 (Password Error)")
    public void postAccount_Create_fail_password() throws Exception{
        mockMvc.perform(
                        post("/register")
                                .param("email", "test@test.com")
                                .param("password", "1234567890")
                                .param("passwordcheck", "12345678")
                                .param("nickname", "Test")
                                .with(csrf())
                )
                .andExpect(model().hasErrors())
                .andExpect(view().name("navbar/create_account"))
                .andExpect(status().isOk());

        Account account = accountRepository.findByNicknameAndChecked("Test", false);
        assertNull(account);
    }

    @Test
    @DisplayName("로그인 계정 생성 - 실패 (Blank Value)")
    public void postAccount_Create_fail_blank() throws Exception{
        mockMvc.perform(
                        post("/register")
                                .param("email", "test@test.com")
                                .param("password", "1234567890")
                                .param("passwordcheck", "12345678")
                                .with(csrf())
                )
                .andExpect(model().hasErrors())
                .andExpect(view().name("navbar/create_account"))
                .andExpect(status().isOk());

        Account account = accountRepository.findByNicknameAndChecked("Test", false);
        assertNull(account);
    }

    @Test
    @DisplayName("로그인 계정 생성 - 실패 (Email_Duplication Error)")
    public void postAccount_Create_fail_duplication_email() throws Exception{
        AccountVO accountVO = new AccountVO();
        accountVO.setEmail("test@test.com");
        accountVO.setPassword("1234567890");
        accountVO.setPasswordcheck("1234567890");
        accountVO.setNickname("Test");
        accountServiceImpl.regAccount(modelMapper.map(accountVO, RegAccountVO.class));

        mockMvc.perform(
                        post("/register")
                                .param("email", "test@test.com")
                                .param("password", "1234567890")
                                .param("passwordcheck", "1234567890")
                                .param("nickname", "test2")
                                .with(csrf())
                )
                .andExpect(model().hasErrors())
                .andExpect(view().name("navbar/create_account"))
                .andExpect(status().isOk());

        Account newAccount = accountRepository.findByNicknameAndChecked("test2", false);
        assertNull(newAccount);
    }

    @Test
    @DisplayName("로그인 계정 생성 - 실패 (Nickname_Duplication Error)")
    public void postAccount_Create_fail_duplication_nickname() throws Exception{
        AccountVO accountVO = new AccountVO();
        accountVO.setEmail("test@test.com");
        accountVO.setPassword("1234567890");
        accountVO.setPasswordcheck("1234567890");
        accountVO.setNickname("Test");
        accountServiceImpl.regAccount(modelMapper.map(accountVO, RegAccountVO.class));

        mockMvc.perform(
                        post("/register")
                                .param("email", "test2@test.com")
                                .param("password", "1234567890")
                                .param("passwordcheck", "1234567890")
                                .param("nickname", "Test")
                                .with(csrf())
                )
                .andExpect(model().hasErrors())
                .andExpect(view().name("navbar/create_account"))
                .andExpect(status().isOk());

        Account newAccount = accountRepository.findByEmailAndChecked("test2@test.com", false);
        assertNull(newAccount);
    }

    @Test
    @DisplayName("로그인 메일 인증 - 성공")
    public void getEmailAuthentication_success() throws Exception {
        AccountVO accountVO = new AccountVO();
        accountVO.setEmail("test@test.com");
        accountVO.setPassword("1234567890");
        accountVO.setPasswordcheck("1234567890");
        accountVO.setNickname("Test");
        Account beforeAccount = modelMapper.map(accountVO, Account.class);
        Account account = accountServiceImpl.regAccount(modelMapper.map(beforeAccount, RegAccountVO.class));
        verify(emailService, times(1)).sendEmailForAuthentication(EmailForAuthenticationVO.getInstance(beforeAccount)); // 메서드 호출 여부 검증

        mockMvc.perform(
                        get("/mailAuth")
                                .param("token", account.getTokenForEmailForAuthentication())
                                .param("email", account.getEmail())
                )
                .andExpect(flash().attributeExists("account"))
                .andExpect(flash().attributeExists("success"))
                .andExpect(redirectedUrl("/"))
                .andExpect(status().is3xxRedirection());
        verify(emailService, times(1)).sendEmailForAuthentication(EmailForAuthenticationVO.getInstance(account)); // 메서드 호출 여부 검증

        Account emailAccount = accountRepository.findByEmailAndChecked("test@test.com", true);
        assertNotNull(emailAccount);

        Account nicknameAccount = accountRepository.findByNicknameAndChecked("Test", true);
        assertNotNull(nicknameAccount);
        assertEquals(emailAccount, nicknameAccount);
    }

    @Test
    @DisplayName("로그인 메일 인증 - 실패(token Error")
    public void getEmailToken_fail_authentecated() throws Exception {
        AccountVO accountVO = new AccountVO();
        accountVO.setEmail("test@test.com");
        accountVO.setPassword("1234567890");
        accountVO.setPasswordcheck("1234567890");
        accountVO.setNickname("Test");
        Account account = accountServiceImpl.regAccount(modelMapper.map(accountVO, RegAccountVO.class));

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

        Account emailAccount = accountRepository.findByEmailAndChecked("test@test.com", true);
        assertNull(emailAccount);
        Account nicknameAccount = accountRepository.findByNicknameAndChecked("Test", true);
        assertNull(nicknameAccount);
    }

    @Test
    @DisplayName("로그인 메일 인증 - 실패(email Error")
    public void getEmailToken_fail_email() throws Exception {
        AccountVO accountVO = new AccountVO();
        accountVO.setEmail("test@test.com");
        accountVO.setPassword("1234567890");
        accountVO.setPasswordcheck("1234567890");
        accountVO.setNickname("Test");
        Account account = accountServiceImpl.regAccount(modelMapper.map(accountVO, RegAccountVO.class));

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

        Account emailAccount = accountRepository.findByEmailAndChecked("test@test.com", true);
        assertNull(emailAccount);
        Account nicknameAccount = accountRepository.findByNicknameAndChecked("Test", true);
        assertNull(nicknameAccount);
    }

    @Test
    @DisplayName("로그인 메일 재인증 - 성공")
    public void reGetEmailAuthentication_success() throws Exception {
        AccountVO accountVO = new AccountVO();
        accountVO.setEmail("test@test.com");
        accountVO.setPassword("1234567890");
        accountVO.setPasswordcheck("1234567890");
        accountVO.setNickname("Test");
        Account account = accountServiceImpl.regAccount(modelMapper.map(accountVO, RegAccountVO.class));
        account.setCreateTimeOfEmailToken(LocalDateTime.now().minusHours(3));

        mockMvc.perform(
                        post("/mailAuthRetry")
                                .param("email", accountVO.getEmail())
                                .with(csrf())
                )
                .andExpect(flash().attributeExists("message"))
                .andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrl("/"));

        Account newAccount = accountRepository.findByEmailAndChecked(accountVO.getEmail(), false);
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
        accountServiceImpl.regAccount(modelMapper.map(accountVO, RegAccountVO.class));

        mockMvc.perform(
                        post("/mailAuthRetry")
                                .param("email", accountVO.getEmail())
                                .with(csrf())
                )
                .andExpect(model().attributeExists("message"))
                .andExpect(model().attributeExists("emailTokenVO"))
                .andExpect(view().name("navbar/token_validation"))
                .andExpect(status().isOk());

        Account account = accountRepository.findByEmailAndChecked(accountVO.getEmail(), false);
        assertNotNull(account);
    }
}
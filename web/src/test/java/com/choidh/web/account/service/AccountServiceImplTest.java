package com.choidh.web.account.service;


import com.choidh.service.account.repository.AccountRepository;
import com.choidh.service.account.service.AccountService;
import com.choidh.service.mail.service.EmailService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.transaction.annotation.Transactional;

import static org.mockito.Mockito.verify;

@Slf4j
@ActiveProfiles("local")
@SpringBootTest
@Transactional
@AutoConfigureMockMvc
@RequiredArgsConstructor(onConstructor = @__(@Autowired))
class AccountServiceImplTest {
    private final AccountService accountService;
    private final AccountRepository accountRepository;
    private final PasswordEncoder passwordEncoder;

    @MockBean
    private EmailService emailService;

    @Test
    @DisplayName("Account 신규 생성 (성공)")
    public void regAccount_Success() throws Exception {
//        Account account = Account.builder()
//                .email("test@test.com")
//                .password("12345678")
//                .nickname("Test")
//                .build();
//        Account savedAccount = accountService.createAccount(account);
//
//        Optional<Account> accountOptional = accountRepository.findById(savedAccount.getId());
//        assertFalse(accountOptional.isEmpty());
//
//        Account getAccountForRepository = accountOptional.get();
//        assertEquals(account, getAccountForRepository);
//        assertFalse(getAccountForRepository.isTokenChecked());
//        assertTrue(passwordEncoder.matches("12345678", getAccountForRepository.getPassword()));
//
//        verify(emailService, times(1)).sendCheckEmail(account); // 메서드 호출 여부 검증
    }

}
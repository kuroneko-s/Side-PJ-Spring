package com.choidh.service.account.repository;

import com.choidh.service.account.entity.Account;
import com.choidh.service.config.WithAccount;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.transaction.annotation.Transactional;

import static org.junit.jupiter.api.Assertions.*;

@Slf4j
@ActiveProfiles("local")
@SpringBootTest
@Transactional
@RequiredArgsConstructor(onConstructor = @__(@Autowired))
class AccountRepositoryTest {
    private final AccountRepository accountRepository;

    @Test
    @DisplayName("Account 단건조회 (성공). By Nickname. Token checked is true")
    @WithAccount("test@test.com")
    public void getAccountByNicknameAndCheckedIsTrue() throws Exception {
        Account account = accountRepository.findByNicknameAndChecked("테스트냥이", true);

        assertNotNull(account);
    }

    @Test
    @DisplayName("Account 단건조회 (실패). By Nickname. Token checked is false")
    @WithAccount("test@test.com")
    public void getAccountByNicknameAndCheckedIsFalse() throws Exception {
        Account account = accountRepository.findByNicknameAndChecked("테스트냥이", false);

        assertNull(account);
    }

    @Test
    @DisplayName("Account 존재 유무 확인 By Email (성공)")
    @WithAccount("test@test.com")
    public void existsAccountByEmail_Success() throws Exception {
        boolean result = accountRepository.existsByEmail("test@test.com");

        assertTrue(result);
    }

    @Test
    @DisplayName("Account 존재 유무 확인 By Email (실패)")
    @WithAccount("test@test.com")
    public void existsAccountByEmail_Fail() throws Exception {
        boolean result = accountRepository.existsByEmail("test2@test.com");

        assertFalse(result);
    }

    @Test
    @DisplayName("Account 존재 유무 확인 By Nickname (성공)")
    @WithAccount("test@test.com")
    public void existsAccountByNickname_Success() throws Exception {
        boolean result = accountRepository.existsByNickname("테스트냥이");

        assertTrue(result);
    }

    @Test
    @DisplayName("Account 존재 유무 확인 By Nickname (실패)")
    @WithAccount("test@test.com")
    public void existsAccountByNickname_Fail() throws Exception {
        boolean result = accountRepository.existsByNickname("테스트냥이2");

        assertFalse(result);
    }

    @Test
    @DisplayName("Account 단건 조회 By Email and Email Authenticated (성공)")
    @WithAccount("test@test.com")
    public void getAccountByEmailAndEmailAuthenticated_Success() throws Exception {
        Account account = accountRepository.findByEmailAndChecked("test@test.com", true);

        assertNotNull(account);
    }

    @Test
    @DisplayName("Account 단건 조회 By Email and Email Authenticated (실패)")
    @WithAccount("test@test.com")
    public void getAccountByEmailAndEmailAuthenticated_Fail() throws Exception {
        Account account = accountRepository.findByEmailAndChecked("test@test.com", false);

        assertNull(account);
    }

    @Test
    @DisplayName("Account 단건 조회 By Nickname and Email Authenticated (성공)")
    @WithAccount("test@test.com")
    public void getAccountByNicknameAndEmailAuthenticated_Success() throws Exception {
        Account account = accountRepository.findByNicknameAndChecked("테스트냥이", true);

        assertNotNull(account);
    }

    @Test
    @DisplayName("Account 단건 조회 By Nickname and Email Authenticated (실패)")
    @WithAccount("test@test.com")
    public void getAccountByNicknameAndEmailAuthenticated_Fail() throws Exception {
        Account account = accountRepository.findByNicknameAndChecked("테스트냥이2", false);

        assertNull(account);
    }

    @Test
    @DisplayName("Account 단건 조회 with learning (성공) - 쿼리 확인용")
    @WithAccount("test@test.com")
    public void findAccountWithLearnings_Success() throws Exception {
        System.out.println("###################"); // 쿼리 확인용
        Account account = accountRepository.findByNicknameAndChecked("테스트냥이", true);
        Account accountWithLearnings = accountRepository.findAccountWithLearnings(account.getId());

        assertNotNull(accountWithLearnings);
    }

    @Test
    @DisplayName("Account 단건 조회 with question (성공) - 쿼리 확인용")
    @WithAccount("test@test.com")
    public void findAccountWithQuestion_Success() throws Exception {
        System.out.println("###################"); // 쿼리 확인용
        Account account = accountRepository.findByNicknameAndChecked("테스트냥이", true);
        Account accountWithQuestion = accountRepository.findAccountWithQuestion(account.getId());

        assertNotNull(accountWithQuestion);
    }

    @Test
    @DisplayName("Account 단건 조회 with Tag (성공) - 쿼리 확인용")
    @WithAccount("test@test.com")
    public void findAccountWithTags_Success() throws Exception {
        System.out.println("###################"); // 쿼리 확인용
        Account account = accountRepository.findByNicknameAndChecked("테스트냥이", true);
        Account accountWithQuestion = accountRepository.findAccountWithTags(account.getId());

        assertNotNull(accountWithQuestion);
    }

//    @Test
//    @DisplayName("Account 단건 조회 with learning, question, listenLearning (성공) - 쿼리 확인용")
//    @WithAccount("test@test.com")
//    public void findAccountWithAll_Success() throws Exception {
//        System.out.println("###################"); // 쿼리 확인용
//        Account account = accountRepository.findByNicknameAndTokenChecked("테스트냥이", true);
//        Account accountWithQuestion = accountRepository.findAccountWithAll(account.getId());
//
//        assertNotNull(accountWithQuestion);
//    }

}
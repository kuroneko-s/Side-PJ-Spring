package com.choidh.service.account.service;

import com.choidh.service.AbstractServiceTestConfig;
import com.choidh.service.account.entity.Account;
import com.choidh.service.professional.entity.ProfessionalAccount;
import com.choidh.service.account.vo.*;
import com.choidh.service.cart.entity.Cart;
import com.choidh.service.cart.repository.CartRepository;
import com.choidh.service.joinTables.entity.LearningCartJoinTable;
import com.choidh.service.joinTables.repository.LearningCartRepository;
import com.choidh.service.learning.entity.Learning;
import com.choidh.service.mail.service.EmailService;
import com.choidh.service.purchaseHistory.service.PurchaseHistoryService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.mock.mockito.MockBean;

import java.time.LocalDateTime;
import java.util.HashSet;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;

@Slf4j
@RequiredArgsConstructor(onConstructor = @__(@Autowired))
class AccountServiceImplTest extends AbstractServiceTestConfig {
    private final AccountService accountService;
    private final CartRepository cartRepository;
    private final LearningCartRepository learningCartRepository;
    private final PurchaseHistoryService purchaseHistoryService;

    @MockBean
    private EmailService emailService;

    @Test
    @DisplayName("Get Account 단건 조회 By Id (성공)")
    public void getAccountById_성공() throws Exception {
        Account testAccount = createAccount();

        theLine();

        Account account = accountService.getAccountById(testAccount.getId());
        assertNotNull(account);
    }

    @Test
    @DisplayName("Get Account 단건 조회 By Id (실패)")
    public void getAccountById_실패() throws Exception {
        assertThrows(IllegalArgumentException.class, () -> {
            accountService.getAccountById(1L);
        });
    }

    @Test
    @DisplayName("Account 단건 조회. By Id With LearningCartJoinTable (성공)")
    public void getAccountByIdWithLearningInCart() throws Exception {
        ProfessionalAccount professionalAccount = createProfessionalAccount(createAccount());

        Account account = createAccount("test2", "test2@test.com");
        Cart cart = cartRepository.save(Cart.builder()
                .account(account)
                .learningCartJoinTables(new HashSet<>())
                .build());
        account.setCart(cart);

        persistClear();

        for (int i = 0; i < 7; i++) {
            Learning learning = createLearning(professionalAccount);

            LearningCartJoinTable learningCartJoinTable = new LearningCartJoinTable();
            learningCartJoinTable.setLearning(learning);

            account.getCart().addLearningCartJoinTable(learningCartJoinTable);

            learningCartRepository.save(learningCartJoinTable);
        }

        theLine();

        Account resultAccount = accountService.getAccountByIdWithLearningInCart(account.getId());
        assertNotNull(resultAccount);

        theLine();

        resultAccount.getCart().getLearningCartJoinTables().forEach(learningCartJoinTable -> {
            Learning learning = learningCartJoinTable.getLearning();
            log.info("{}", learning.getId());
        });
    }

    @Test
    @DisplayName("Account 단건 조회 By Id With PurchaseHistories Learning")
    public void getAccountByIdWithPurchaseHistories() throws Exception {
        ProfessionalAccount testProfessionalAccount = createProfessionalAccount(createAccount());

        Account testAccount = createAccount("sample2", "sample2@test.com");
        for (int i = 0; i < 12; i++) {
            Learning learning = createLearning(testProfessionalAccount);
            purchaseHistoryService.regPurchaseHistory(testAccount.getId(), learning.getId());
        }

        theLine();

        Account resultAccount = accountService.getAccountByIdWithPurchaseHistories(testAccount.getId());
        assertNotNull(resultAccount);
        resultAccount.getPurchaseHistories().forEach(Assertions::assertNotNull);
    }

    @Test
    @DisplayName("Reg Account 생성")
    public void regAccount() throws Exception {
        Account accountVO = accountService.regAccount(RegAccountVO.builder()
                .nickname("sample")
                .email("sample")
                .password(passwordEncoder.encode("1234567890"))
                .passwordcheck(passwordEncoder.encode("1234567890"))
                .build());

        theLine();

        Optional<Account> byId = accountRepository.findById(accountVO.getId());
        assertFalse(byId.isEmpty());
        Account account = byId.get();
        assertNotNull(account);

        assertNotNull(account.getCart());
        assertNotNull(account.getTags());
        assertNotNull(account.getReviews());
        assertNotNull(account.getPurchaseHistories());
        assertNotNull(account.getQuestions());
    }

    @Test
    @DisplayName("인증용 이메일 전송 (인증완료 계정)")
    public void sendEmailForAuthentication_1() throws Exception {
        Account account = createAccount("sample", "sample@test.com");

        theLine();

        boolean result = accountService.sendEmailForAuthentication("sample@test.com");
        assertFalse(result);
    }

    @Test
    @DisplayName("인증용 이메일 전송 (미인증 계정)")
    public void sendEmailForAuthentication_2() throws Exception {
        Account account = createAccount("sample", "sample@test.com");
        account.setCreateTimeOfEmailToken(LocalDateTime.of(2024, 5, 4, 10, 10));
        account.setChecked(false);

        theLine();

        boolean result = accountService.sendEmailForAuthentication("sample@test.com");
        assertTrue(result);
    }

    @Test
    @DisplayName("이메일 인증")
    public void verifyingEmail() throws Exception {
        Account account = createAccount("sample", "sample@test.com");
        account.setChecked(false);

        assertFalse(account.isChecked());

        accountService.verifyingEmail(account.getTokenForEmailForAuthentication(), "sample@test.com");

        theLine();

        assertTrue(account.isChecked());
    }

    @Test
    @DisplayName("Mod 프로필 수정")
    public void modAccount() throws Exception {
        Account account = createAccount("sample", "sample@test.com");

        theLine();

        ModAccountVO modAccountVO = ModAccountVO.builder()
                .nickname("custom nickname")
                .description("custom description")
                .build();
        Account newAccount = accountService.modAccount(modAccountVO, account.getId());

        assertNotEquals(account.getNickname(), newAccount.getNickname());
        assertNotEquals(account.getDescription(), newAccount.getDescription());
        assertEquals(newAccount.getNickname(), "custom nickname");
        assertEquals(newAccount.getDescription(), "custom description");
    }

    @Test
    @DisplayName("Mod 패스워드 수정 (성공)")
    public void modPassword() throws Exception {
        Account account = createAccount();

        theLine();

        ModPasswordVO modPasswordVO = ModPasswordVO.builder()
                .nowPassword("1234567890")
                .newPassword("sample1234")
                .newPasswordCheck("sample1234")
                .build();
        Account newAccount = accountService.modPassword(modPasswordVO, account.getId());

        assertTrue(
                passwordEncoder.matches("sample1234", newAccount.getPassword())
        );
    }

    @Test
    @DisplayName("Mod 패스워드 수정 (실패 - 기존 패스워드)")
    public void modPassword_2() throws Exception {
        Account account = createAccount();

        theLine();

        ModPasswordVO modPasswordVO = ModPasswordVO.builder()
                .nowPassword("1234567891")
                .newPassword("sample1234")
                .newPasswordCheck("sample1234")
                .build();

        assertThrows(IllegalArgumentException.class, () -> {
            accountService.modPassword(modPasswordVO, account.getId());
        });
    }

    @Test
    @DisplayName("Mod 패스워드 수정 (실패 - 새로운 패스워드)")
    public void modPassword_3() throws Exception {
        Account account = createAccount();

        theLine();

        ModPasswordVO modPasswordVO = ModPasswordVO.builder()
                .nowPassword("1234567890")
                .newPassword("sample1234")
                .newPasswordCheck("sample1235")
                .build();

        assertThrows(IllegalArgumentException.class, () -> {
            accountService.modPassword(modPasswordVO, account.getId());
        });
    }

    @Test
    @DisplayName("Mod 패스워드 수정 (실패 - 새로운 패스워드-2)")
    public void modPassword_4() throws Exception {
        Account account = createAccount();

        theLine();

        ModPasswordVO modPasswordVO = ModPasswordVO.builder()
                .nowPassword("1234567890")
                .newPassword("sample1235")
                .newPasswordCheck("sample1234")
                .build();

        assertThrows(IllegalArgumentException.class, () -> {
            accountService.modPassword(modPasswordVO, account.getId());
        });
    }

    @Test
    @DisplayName("Mod 알림 설정 수정")
    public void modNotifications() throws Exception {
        Account account = createAccount();
        ModNotificationVO modNotificationVO = ModNotificationVO.builder()
                .siteMailNotification(true)
                .siteWebNotification(true)
                .learningMailNotification(true)
                .learningWebNotification(true)
                .build();

        theLine();
        Account newAccount = accountService.modNotifications(modNotificationVO, account.getId());

        assertTrue(newAccount.isSiteMailNotification());
        assertTrue(newAccount.isSiteWebNotification());
        assertTrue(newAccount.isLearningMailNotification());
        assertTrue(newAccount.isLearningWebNotification());
    }

    @Test
    @DisplayName("Mod 알림 설정 수정-2")
    public void modNotifications_2() throws Exception {
        Account account = createAccount();
        ModNotificationVO modNotificationVO = ModNotificationVO.builder()
                .siteMailNotification(false)
                .siteWebNotification(false)
                .learningMailNotification(false)
                .learningWebNotification(false)
                .build();

        theLine();

        Account newAccount = accountService.modNotifications(modNotificationVO, account.getId());

        assertFalse(newAccount.isSiteMailNotification());
        assertFalse(newAccount.isSiteWebNotification());
        assertFalse(newAccount.isLearningMailNotification());
        assertFalse(newAccount.isLearningWebNotification());
    }

    @Test
    @DisplayName("GET 단건 조회 For 프로필 화면용")
    public void getAccountForProfile() throws Exception {
        Account account = createAccount();

        theLine();

        ProfileVO profileVO = accountService.getAccountForProfile(account.getId());

        assertNotNull(profileVO);
    }
}
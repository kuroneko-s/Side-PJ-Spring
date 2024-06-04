package com.choidh.service.account.repository;

import com.choidh.service.account.entity.Account;
import com.choidh.service.account.entity.ProfessionalAccount;
import com.choidh.service.cart.service.CartService;
import com.choidh.service.config.WithAccount;
import com.choidh.service.joinTables.entity.LearningCartJoinTable;
import com.choidh.service.learning.entity.Learning;
import com.choidh.service.learning.repository.LearningRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.test.annotation.Rollback;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.transaction.annotation.Transactional;

import javax.persistence.EntityManager;
import java.time.LocalDateTime;
import java.util.HashSet;

import static org.junit.jupiter.api.Assertions.*;

@Slf4j
@ActiveProfiles("local")
@SpringBootTest
@Transactional
@Rollback(value = false)
@RequiredArgsConstructor(onConstructor = @__(@Autowired))
class AccountRepositoryTest {
    private final AccountRepository accountRepository;
    private final CartService cartService;
    private final EntityManager entityManager;

    private final PasswordEncoder passwordEncoder;
    private final LearningRepository learningRepository;
    private final ProfessionalAccountRepository professionalAccountRepository;

    private Account createAccount(String nickname, String email) {
        Account account = Account.builder()
                .nickname(nickname)
                .email(email)
                .password(passwordEncoder.encode("1234567890"))
                .checked(true)
                .tags(new HashSet<>())
                .questions(new HashSet<>())
                .reviews(new HashSet<>())
                .purchaseHistories(new HashSet<>())
                .build();
        account.createTokenForEmailForAuthentication();

        return accountRepository.save(account);
    }

    private ProfessionalAccount createProfessionalAccount(Account account) {
        return professionalAccountRepository.save(ProfessionalAccount.builder()
                .account(account)
                .name("강사 이름")
                .description("개잘나가는 강사임")
                .history("개쩌는 경력들")
                .build());
    }

    private Learning createLearning(ProfessionalAccount professionalAccount) {
        Learning learning = Learning.builder()
                .title("샘플 강의 1")
                .simpleSubscription("간단한 강의 설명")
                .subscription("장황한 강의 설명글")
                .mainCategory("자바")
                .mainCategory("알고리즘")
                .price(10000)
                .rating((int) Math.floor(Math.random() * 5) + 1)
                .opening(true)
                .openingDate(LocalDateTime.now())
                .attachmentGroup(null)
                .professionalAccount(professionalAccount)
                .tags(new HashSet<>())
                .questions(new HashSet<>())
                .reviews(new HashSet<>())
                .purchaseHistories(new HashSet<>())
                .carts(new HashSet<>())
                .build();

        return learningRepository.save(learning);
    }

    private void persistClear() {
        entityManager.flush();
        entityManager.clear();
    }

    private void theLine() {
        persistClear();

        log.info("################## THE LINE ##################");
        log.info("################## THE LINE ##################");
        log.info("################## THE LINE ##################");
        log.info("################## THE LINE ##################");
        log.info("################## THE LINE ##################");
        log.info("################## THE LINE ##################");
        log.info("################## THE LINE ##################");
        log.info("################## THE LINE ##################");
        log.info("################## THE LINE ##################");
        log.info("################## THE LINE ##################");
        log.info("################## THE LINE ##################");
    }

    @Test
    @DisplayName("Account 단건 조회. By Id With Learning In Cart")
    public void findAccountByIdWithLearning() throws Exception {
        Account account = createAccount("테스트냥이", "test@test.com");
        ProfessionalAccount professionalAccount = createProfessionalAccount(createAccount("테스트냥이2", "test2@test.com"));
        int count = 0;

        cartService.regCart(account.getId());
        for (int i = 0; i < 20; i++) {
            Learning learning = createLearning(professionalAccount);

            if (i % 2 == 0) {
                cartService.addCart(account.getId(), learning.getId());
                count++;
            }
        }

        theLine();

        Account result = accountRepository.findAccountByIdWithLearning(account.getId());

        persistClear();

        assertNotNull(result);
        assertNotNull(result.getCart());
        assertFalse(result.getCart().getLearningCartJoinTables().isEmpty());
        assertEquals(result.getCart().getLearningCartJoinTables().size(), count);

        theLine();

        for (LearningCartJoinTable learningCartJoinTable : result.getCart().getLearningCartJoinTables()) {
            log.info(learningCartJoinTable.getLearning().getProfessionalAccount().toString());
        }
    }

    @Test
    @DisplayName("이메일 중복여부 확인 (성공)")
    @WithAccount("test@test.com")
    public void existsAccountByEmail_Success() throws Exception {
        theLine();

        boolean result = accountRepository.existsByEmail("test@test.com");

        assertTrue(result);
    }

    @Test
    @DisplayName("이메일 중복여부 확인 (실패)")
    @WithAccount("test@test.com")
    public void existsAccountByEmail_Fail() throws Exception {
        theLine();

        boolean result = accountRepository.existsByEmail("test2@test.com");

        assertFalse(result);
    }

    @Test
    @DisplayName("닉네임 중복여부 확인 (성공)")
    @WithAccount("test@test.com")
    public void existsAccountByNickname_Success() throws Exception {
        theLine();

        boolean result = accountRepository.existsByNickname("테스트냥이");

        assertTrue(result);
    }

    @Test
    @DisplayName("닉네임 중복여부 확인 (실패)")
    @WithAccount("test@test.com")
    public void existsAccountByNickname_Fail() throws Exception {
        theLine();

        boolean result = accountRepository.existsByNickname("테스트냥이2");

        assertFalse(result);
    }

    @Test
    @DisplayName("Account 단건 조회 By Email and Email Authenticated (성공)")
    @WithAccount("test@test.com")
    public void getAccountByEmailAndEmailAuthenticated_Success() throws Exception {
        theLine();

        Account account = accountRepository.findByEmailAndChecked("test@test.com", true);

        assertNotNull(account);
    }

    @Test
    @DisplayName("Account 단건 조회 By Email and Email Authenticated (실패)")
    @WithAccount("test@test.com")
    public void getAccountByEmailAndEmailAuthenticated_Fail() throws Exception {
        theLine();

        Account account = accountRepository.findByEmailAndChecked("test@test.com", false);

        assertNull(account);
    }

    @Test
    @DisplayName("Account 단건 조회 (성공). By Nickname. Token checked is true")
    @WithAccount("test@test.com")
    public void getAccountByNicknameAndCheckedIsTrue() throws Exception {
        theLine();

        Account account = accountRepository.findByNicknameAndChecked("테스트냥이", true);

        assertNotNull(account);
    }

    @Test
    @DisplayName("Account 단건 조회 (실패). By Nickname. Token checked is false")
    @WithAccount("test@test.com")
    public void getAccountByNicknameAndCheckedIsFalse() throws Exception {
        theLine();

        Account account = accountRepository.findByNicknameAndChecked("테스트냥이", false);

        assertNull(account);
    }

    @Test
    @DisplayName("Account 단건 조회 By Nickname and Email Authenticated (실패)")
    @WithAccount("test@test.com")
    public void getAccountByNicknameAndEmailAuthenticated_Fail() throws Exception {
        theLine();

        Account account = accountRepository.findByNicknameAndChecked("테스트냥이2", false);

        assertNull(account);
    }

    @Test
    @DisplayName("Account 단건 조회 with learning (성공) - 쿼리 확인용")
    @WithAccount("test@test.com")
    public void findAccountWithLearnings_Success() throws Exception {
        Account testAccount = accountRepository.findByNicknameAndChecked("테스트냥이", true);
        theLine();

        Account account = accountRepository.findAccountWithLearnings(testAccount.getId());

        assertNotNull(account);

        theLine();

        account.getPurchaseHistories().forEach(purchaseHistory -> {
            log.info(purchaseHistory.toString());
            log.info(purchaseHistory.getLearning().toString());
        });
    }

    @Test
    @DisplayName("Account 단건 조회 with Questions (성공) - 쿼리 확인용")
    @WithAccount("test@test.com")
    public void findAccountWithQuestion_Success() throws Exception {
        Account testAccount = accountRepository.findByNicknameAndChecked("테스트냥이", true);

        theLine();

        Account account = accountRepository.findAccountWithQuestion(testAccount.getId());

        assertNotNull(account);

        account.getQuestions().forEach(question -> {
            log.info(question.toString());
        });
    }

    @Test
    @DisplayName("Account 단건 조회 with Tags (성공) - 쿼리 확인용")
    @WithAccount("test@test.com")
    public void findAccountWithTags_Success() throws Exception {
        Account testAccount = accountRepository.findByNicknameAndChecked("테스트냥이", true);

        theLine();

        Account account = accountRepository.findAccountWithTags(testAccount.getId());

        assertNotNull(account);

        account.getTags().forEach(accountTagJoinTable -> {
            log.info(accountTagJoinTable.toString());
            log.info(accountTagJoinTable.getTag().getTitle());
        });
    }

    @Test
    @DisplayName("Account 단건 조회 For 프로필 화면용 (성공) - 쿼리 확인용")
    @WithAccount("test@test.com")
    public void findAccountWithAll_Success() throws Exception {
        Account testAccount = accountRepository.findByNicknameAndChecked("테스트냥이", true);

        theLine();

        Account account = accountRepository.findAccountForProfile(testAccount.getId());

        assertNotNull(account);

        theLine();

        log.info(account.getCart().toString());
        account.getTags().forEach(accountTagJoinTable -> {
            log.info(accountTagJoinTable.toString());
            log.info(accountTagJoinTable.getTag().getTitle());
        });

        account.getPurchaseHistories().forEach(purchaseHistory -> {
            log.info(purchaseHistory.toString());
            log.info(purchaseHistory.getLearning().toString());
        });

        account.getQuestions().forEach(question -> {
            log.info(question.toString());
        });
    }

}
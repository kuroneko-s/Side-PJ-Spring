package com.choidh.service;

import com.choidh.service.account.entity.Account;
import com.choidh.service.professional.entity.ProfessionalAccount;
import com.choidh.service.account.repository.AccountRepository;
import com.choidh.service.professional.repository.ProfessionalAccountAccountRepository;
import com.choidh.service.cart.entity.Cart;
import com.choidh.service.cart.service.CartService;
import com.choidh.service.learning.entity.Learning;
import com.choidh.service.learning.repository.LearningRepository;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.transaction.annotation.Transactional;

import javax.persistence.EntityManager;
import java.time.LocalDateTime;
import java.util.HashSet;

@Slf4j
@ActiveProfiles("local")
@Transactional
@SpringBootTest
public abstract class AbstractServiceTestConfig {
    @Autowired protected AccountRepository accountRepository;
    @Autowired protected EntityManager entityManager;
    @Autowired protected PasswordEncoder passwordEncoder;
    @Autowired protected LearningRepository learningRepository;
    @Autowired protected ProfessionalAccountAccountRepository professionalAccountRepository;
    @Autowired protected CartService cartService;

    protected Account createAccount() {
        Account account = new Account();
        account.setNickname("테스트냥이");
        account.setEmail("test@test.com");
        account.setPassword(passwordEncoder.encode("1234567890"));
        account.setChecked(true);
        account.createTokenForEmailForAuthentication();
        Account newAccount = accountRepository.save(account);

        Cart cart = cartService.regCart(newAccount.getId());
        account.setCart(cart);

        return newAccount;
    }

    protected Account createAccount(String nickname, String email) {
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

    protected ProfessionalAccount createProfessionalAccount(Account account) {
        return professionalAccountRepository.save(ProfessionalAccount.builder()
                .account(account)
                .name("강사 이름")
                .description("개잘나가는 강사임")
                .history("개쩌는 경력들")
                .learningSet(new HashSet<>())
                .build());
    }

    protected Learning createLearning(ProfessionalAccount professionalAccount) {
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

    protected void persistClear() {
        entityManager.flush();
        entityManager.clear();
    }

    protected void theLine() {
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
}

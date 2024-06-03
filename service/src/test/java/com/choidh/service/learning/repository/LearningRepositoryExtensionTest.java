package com.choidh.service.learning.repository;

import com.choidh.service.account.entity.Account;
import com.choidh.service.account.entity.ProfessionalAccount;
import com.choidh.service.account.repository.AccountRepository;
import com.choidh.service.account.repository.ProfessionalAccountRepository;
import com.choidh.service.joinTables.entity.LearningTagJoinTable;
import com.choidh.service.joinTables.repository.LearningTagRepository;
import com.choidh.service.learning.entity.Learning;
import com.choidh.service.tag.entity.Tag;
import com.choidh.service.tag.service.TagService;
import com.choidh.service.tag.vo.RegTagVO;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.test.annotation.Rollback;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.transaction.annotation.Transactional;

import javax.persistence.EntityManager;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

/**
 * 쿼리 확인용일시 {@link Rollback} 활성화 필요
 */

@Slf4j
@ActiveProfiles("local")
@SpringBootTest
@Transactional
@RequiredArgsConstructor(onConstructor = @__(@Autowired))
public class LearningRepositoryExtensionTest {
    private final LearningRepository learningRepository;
    private final PasswordEncoder passwordEncoder;
    private final AccountRepository accountRepository;
    private final ProfessionalAccountRepository professionalAccountRepository;
    private final EntityManager entityManager;
    private final TagService tagService;
    private final LearningTagRepository learningTagRepository;

    private Account createAccount() {
        Account account = new Account();
        account.setNickname("테스트냥이");
        account.setEmail("test@test.com");
        account.setPassword(passwordEncoder.encode("1234567890"));
        account.setChecked(true);
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
        int floor = (int) Math.floor(Math.random() * 2);

        Learning learning = Learning.builder()
                .title("샘플 강의 1")
                .simpleSubscription("간단한 강의 설명")
                .subscription("장황한 강의 설명글")
                .mainCategory(floor % 2 == 0? "자바" : "자바스크립트")
                .subCategory("알고리즘")
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
                .build();

        return learningRepository.save(learning);
    }

    private Learning createLearning(ProfessionalAccount professionalAccount, String title) {
        int floor = (int) Math.floor(Math.random() * 2);

        Learning learning = Learning.builder()
                .title(title)
                .simpleSubscription("간단한 강의 설명")
                .subscription("장황한 강의 설명글")
                .mainCategory(floor % 2 == 0? "자바" : "자바스크립트")
                .subCategory("알고리즘")
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
    @DisplayName("Learning 페이징. By Keyword")
    public void findByKeywordWithPageable() throws Exception {
        ProfessionalAccount professionalAccount = createProfessionalAccount(createAccount());

        for (var i = 0; i < 30; i++) {
            createLearning(professionalAccount, "sample title " + i);
        }

        Page<Learning> resultList = learningRepository.findByKeywordWithPageable("sample", PageRequest.of(
                0,
                16,
                Sort.by(Sort.Direction.DESC, "openingDate"))
        );

        assertFalse(resultList.getContent().isEmpty());
    }

    @Test
    @DisplayName("Learning 페이징. By 카테고리")
    public void findByCategoryWithPageable() throws Exception {
        ProfessionalAccount professionalAccount = createProfessionalAccount(createAccount());

        for (var i = 0; i < 30; i++) {
            createLearning(professionalAccount);
        }

        Page<Learning> resultList = learningRepository.findByCategoryWithPageable("자바", PageRequest.of(
                0,
                16,
                Sort.by(Sort.Direction.DESC, "openingDate"))
        );

        assertFalse(resultList.getContent().isEmpty());
        assertNotEquals(resultList.getContent().size(), 30);
    }

    @Test
    @DisplayName("Learning 페이징. By 카레고리, Keyword")
    public void findByCategoryAndKeywordWithPageable() throws Exception {
        ProfessionalAccount professionalAccount = createProfessionalAccount(createAccount());

        for (var i = 0; i < 60; i++) {
            int floor = (int) Math.floor(Math.random() * 3);
            Learning learning = createLearning(professionalAccount, "sample title " + floor);
        }

        theLine();

        Page<Learning> resultList = learningRepository.findByCategoryAndKeywordWithPageable("sample title 0", "자바", PageRequest.of(
                0,
                16,
                Sort.by(Sort.Direction.DESC, "openingDate"))
        );

        assertFalse(resultList.isEmpty());
        assertNotEquals(resultList.getSize(), 30);
    }

    @Test
    @DisplayName("Learning 목록조회. By Tag 목록.")
    public void findTop12ByTagsOrderByRatingDesc() throws Exception {
        ProfessionalAccount professionalAccount = createProfessionalAccount(createAccount());
        List<Tag> tagList = new ArrayList<>();

        for (var i = 0; i < 60; i++) {
            int floor = (int) Math.floor(Math.random() * 3);
            Learning learning = createLearning(professionalAccount, "sample title " + floor);

            // Tag 생성
            Tag tag = tagService.regTag(RegTagVO.builder()
                    .title("tag " + i)
                    .build());

            if (i % 3 == 0) tagList.add(tag);

            LearningTagJoinTable learningTagJoinTable = LearningTagJoinTable.builder()
                    .learning(learning)
                    .tag(tag)
                    .build();
            learningTagRepository.save(learningTagJoinTable);

            learning.setTags(learningTagJoinTable);
        }

        theLine();

        List<Learning> result = learningRepository.findTop12ByTagsOrderByRatingDesc(tagList);

        assertFalse(result.isEmpty());
        assertEquals(tagList.size(), 20);
        assertEquals(result.size(), 12);
    }
}
package com.choidh.service.learning.repository;

import com.choidh.service.AbstractRepositoryTestConfig;
import com.choidh.service.account.entity.ProfessionalAccount;
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
import org.springframework.test.annotation.Rollback;

import java.util.ArrayList;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Learning Repository 쿼리 확인용 테스트
 * 쿼리 확인용일시 {@link Rollback} 활성화 필요
 */

@Slf4j
// @Rollback(value = false)
@RequiredArgsConstructor(onConstructor = @__(@Autowired))
class LearningRepositoryTest extends AbstractRepositoryTestConfig {
    private final LearningRepository learningRepository;
    private final LearningTagRepository learningTagRepository;
    private final TagService tagService;

    @Test
    @DisplayName("기본 강의 생성 - 성공")
    public void createLearningSuccess() throws Exception {
        ProfessionalAccount professionalAccount = createProfessionalAccount(createAccount());
        Learning learning = this.createLearning(professionalAccount);

        theLine();

        assertNotNull(learning);
    }

    @Test
    @DisplayName("Learning 단건 조회. By Learning PK")
    public void findByIdWithTags() throws Exception {
        // Learning 생성
        ProfessionalAccount professionalAccount = createProfessionalAccount(createAccount());
        Learning learning = this.createLearning(professionalAccount);

        // Tag 생성
        Tag tag = tagService.regTag(RegTagVO.builder()
                .title("tag 1")
                .build());

        LearningTagJoinTable learningTagJoinTable = LearningTagJoinTable.builder()
                .learning(learning)
                .tag(tag)
                .build();
        learningTagRepository.save(learningTagJoinTable);

        learning.setTags(learningTagJoinTable);

        theLine();

        // Learning 단건 조회. By Learning PK
        Learning result = learningRepository.findByIdWithTags(learning.getId());

        assertNotNull(result);
        for (LearningTagJoinTable v : result.getTags()) {
            assertEquals(v.getTag().getTitle(), "tag 1");
        }
    }

    @Test
    @DisplayName("Learning 목록조회. By Learning PK")
    public void findListByLearningIdsWithTags() throws Exception {
        List<Learning> targetLearning = new ArrayList<>();
        Tag tag = tagService.regTag(RegTagVO.builder()
                .title("tag 1")
                .build());

        ProfessionalAccount professionalAccount = createProfessionalAccount(createAccount());
        for (int i = 0; i < 10; i++) {
            Learning learning = this.createLearning(professionalAccount);
            targetLearning.add(learning);

            LearningTagJoinTable learningTagJoinTable = LearningTagJoinTable.builder()
                    .learning(learning)
                    .tag(tag)
                    .build();
            learningTagRepository.save(learningTagJoinTable);
            learning.setTags(learningTagJoinTable);
        }

        theLine();

        List<Learning> result = learningRepository.findListByLearningIdsWithTags(targetLearning);

        assertFalse(result.isEmpty());
        assertEquals(result.size(), 10);
    }

    @Test
    @DisplayName("Learning 목록조회 Limit 4. By Opening is true and OpningDate DESC 정렬")
    public void findTop12ByOpeningIsTrueOrderByOpeningDateDesc() throws Exception {
        Tag tag = tagService.regTag(RegTagVO.builder()
                .title("tag 1")
                .build());

        ProfessionalAccount professionalAccount = createProfessionalAccount(createAccount());
        for (int i = 0; i < 20; i++) {
            Learning learning = this.createLearning(professionalAccount);

            LearningTagJoinTable learningTagJoinTable = LearningTagJoinTable.builder()
                    .learning(learning)
                    .tag(tag)
                    .build();
            learningTagRepository.save(learningTagJoinTable);
            learning.setTags(learningTagJoinTable);

            Thread.sleep(500);
        }

        theLine();

        List<Learning> learningList = learningRepository.findTop12ByOpeningIsTrueOrderByOpeningDateDesc();

        assertEquals(learningList.size(), 12);
        assertTrue(learningList.get(0).getOpeningDate().isAfter(learningList.get(1).getOpeningDate()));
    }

    @Test
    @DisplayName("Learning 목록조회 Limit 12. By Opening is true and Rating DESC 정렬")
    public void findTop12ByOpeningOrderByRatingDesc() throws Exception {
        Tag tag = tagService.regTag(RegTagVO.builder()
                .title("tag 1")
                .build());

        ProfessionalAccount professionalAccount = createProfessionalAccount(createAccount());
        for (int i = 0; i < 20; i++) {
            Learning learning = this.createLearning(professionalAccount);

            LearningTagJoinTable learningTagJoinTable = LearningTagJoinTable.builder()
                    .learning(learning)
                    .tag(tag)
                    .build();
            learningTagRepository.save(learningTagJoinTable);
            learning.setTags(learningTagJoinTable);
        }

        theLine();

        List<Learning> learningList = learningRepository.findTop12ByOpeningOrderByRatingDesc(true);

        assertEquals(learningList.size(), 12);
        assertTrue(learningList.get(0).getRating() > learningList.get(11).getRating());
        assertTrue(learningList.get(11).getRating() < 5);
    }

    @Test
    @DisplayName("Learning 상세 조회. By Learning PK")
    public void findLearningDetailById() throws Exception {
        ProfessionalAccount professionalAccount = createProfessionalAccount(createAccount());
        Learning learning = createLearning(professionalAccount);

        theLine();

        Learning result = learningRepository.findLearningDetailById(learning.getId());

        assertNotNull(result);
    }

    @Test
    @DisplayName("Learning 단건 조회 By Learning PK With Question")
    public void findLearningByIdWithQuestions() throws Exception {
        ProfessionalAccount professionalAccount = createProfessionalAccount(createAccount());
        Learning learning = createLearning(professionalAccount);

        theLine();

        Learning result = learningRepository.findLearningByIdWithQuestions(learning.getId());

        assertNotNull(result);

        persistClear();
        log.info(result.getQuestions().toString());
    }

    @Test
    @DisplayName("Learning 목록 조회 By Learning PKs")
    public void findLearningListByLearningIdList() throws Exception {
        List<Long> learingIdList = new ArrayList<>();
        ProfessionalAccount professionalAccount = createProfessionalAccount(createAccount());

        for (int i = 0; i < 10; i++) {
            Learning learning = this.createLearning(professionalAccount);

            if (learning.getId() % 2 == 0) learingIdList.add(learning.getId());
        }

        theLine();

        List<Learning> resultList = learningRepository.findLearningListByLearningIdList(learingIdList);

        assertNotNull(resultList);
        assertFalse(resultList.isEmpty());

        for (Learning learning : resultList) {
            assertTrue(learingIdList.contains(learning.getId()));
            assertEquals(0, learning.getId() % 2);
        }
    }
}
package com.choidh.service.joinTables.repository;

import com.choidh.service.AbstractRepositoryTestConfig;
import com.choidh.service.account.entity.Account;
import com.choidh.service.professional.entity.ProfessionalAccount;
import com.choidh.service.joinTables.entity.LearningTagJoinTable;
import com.choidh.service.learning.entity.Learning;
import com.choidh.service.tag.entity.Tag;
import com.choidh.service.tag.repository.TagRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.Set;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotEquals;

@Slf4j
//@Rollback(value = false)
@RequiredArgsConstructor(onConstructor = @__(@Autowired))
class LearningTagRepositoryTest extends AbstractRepositoryTestConfig {
    private final LearningTagRepository learningTagRepository;
    private final TagRepository tagRepository;

    @Test
    @DisplayName("LearningTagJoinTable 목록 조회. By Learning Id")
    public void findListByLearningId() throws Exception {
        Account account = createAccount();
        ProfessionalAccount professionalAccount = createProfessionalAccount(account);
        Learning learning = createLearning(professionalAccount);

        for (int i = 0; i < 15; i++) {
            LearningTagJoinTable learningTagJoinTable = new LearningTagJoinTable();

            Tag tag = tagRepository.save(Tag.builder()
                    .title("title " + i)
                    .build());

            learningTagJoinTable.setLearning(learning);
            learningTagJoinTable.setTag(tag);

            learningTagRepository.save(learningTagJoinTable);
        }

        theLine();

        Set<LearningTagJoinTable> learningTagJoinTableList = learningTagRepository.findListByLearningId(learning.getId());

        assertEquals(learningTagJoinTableList.size(), 15);
        learningTagJoinTableList.forEach(learningTagJoinTable -> assertEquals(learningTagJoinTable.getLearning(), learning));
    }

    @Test
    @DisplayName("LearningTagJoinTable 삭제. By LearningTagJoinTable Id")
    public void deleteByLearningIdAndTagTitle() throws Exception {
        Account account = createAccount();
        ProfessionalAccount professionalAccount = createProfessionalAccount(account);
        Learning learning = createLearning(professionalAccount);
        LearningTagJoinTable target = null;

        for (int i = 0; i < 15; i++) {
            LearningTagJoinTable learningTagJoinTable = new LearningTagJoinTable();

            Tag tag = tagRepository.save(Tag.builder()
                    .title("title " + i)
                    .build());

            learningTagJoinTable.setLearning(learning);
            learningTagJoinTable.setTag(tag);

            learningTagJoinTable = learningTagRepository.save(learningTagJoinTable);
            if (i == 7) {
                target = learningTagJoinTable;
            }
        }

        theLine();

        int deleteResult = learningTagRepository.deleteByLearningIdAndTagTitle(target.getId());
        assertEquals(deleteResult, 1);

        Set<LearningTagJoinTable> learningTagJoinTableSet = learningTagRepository.findListByLearningId(learning.getId());
        for (LearningTagJoinTable learningTagJoinTable : learningTagJoinTableSet) {
            assertNotEquals(learningTagJoinTable, target);
        }
    }
}
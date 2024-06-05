package com.choidh.service.joinTables.repository;

import com.choidh.service.AbstractRepositoryTestConfig;
import com.choidh.service.account.entity.Account;
import com.choidh.service.account.entity.ProfessionalAccount;
import com.choidh.service.joinTables.entity.LearningCartJoinTable;
import com.choidh.service.learning.entity.Learning;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

import static org.junit.jupiter.api.Assertions.assertEquals;

@Slf4j
//@Rollback(value = false)
@RequiredArgsConstructor(onConstructor = @__(@Autowired))
class LearningCartRepositoryTest extends AbstractRepositoryTestConfig {
    private final LearningCartRepository learningCartRepository;

    @Test
    @DisplayName("LearningCartJoinTable 삭제. By Cart Id And Learning Id")
    public void deleteByCartIdAndLearningId() throws Exception {
        Account account = createAccount();
        ProfessionalAccount professionalAccount = createProfessionalAccount(account);
        List<LearningCartJoinTable> savedList = new ArrayList<>();
        LearningCartJoinTable target = null;

        for (int i = 0; i < 10; i++) {
            Learning learning = createLearning(professionalAccount);

            LearningCartJoinTable learningCartJoinTable = new LearningCartJoinTable();
            learningCartJoinTable.setLearning(learning);

            account.getCart().addLearningCartJoinTable(learningCartJoinTable);

            savedList.add(learningCartRepository.save(learningCartJoinTable));

            if (i == 7) {
                target = learningCartJoinTable;
            }
        }

        theLine();

        int deleteResult = learningCartRepository.deleteByCartIdAndLearningId(account.getCart().getId(), target.getLearning().getId());
        assertEquals(deleteResult, 1);

        theLine();

        Set<LearningCartJoinTable> learningCartJoinTableList = learningCartRepository.findListWithLearningByCartId(account.getCart().getId());

        assertEquals(learningCartJoinTableList.size(), 9);
        List<LearningCartJoinTable> resultList = savedList.stream().filter(learningCartJoinTable -> !learningCartJoinTableList.contains(learningCartJoinTable)).collect(Collectors.toList());
        assertEquals(resultList.size(), 1);
        assertEquals(resultList.get(0), target);
    }

    @Test
    @DisplayName("LearningCartJoinTable 목록 조회. By Cart Id")
    public void findListWithLearningByCartId() throws Exception {
        Account account = createAccount();
        ProfessionalAccount professionalAccount = createProfessionalAccount(account);

        for (int i = 0; i < 25; i++) {
            Learning learning = createLearning(professionalAccount);

            LearningCartJoinTable learningCartJoinTable = new LearningCartJoinTable();
            learningCartJoinTable.setLearning(learning);

            account.getCart().addLearningCartJoinTable(learningCartJoinTable);

            learningCartRepository.save(learningCartJoinTable);
        }

        theLine();

        Set<LearningCartJoinTable> learningCartJoinTableList = learningCartRepository.findListWithLearningByCartId(account.getCart().getId());

        assertEquals(learningCartJoinTableList.size(), 25);
    }
}
package com.choidh.service.account.repository;

import com.choidh.service.AbstractRepositoryTestConfig;
import com.choidh.service.account.entity.Account;
import com.choidh.service.account.entity.ProfessionalAccount;
import com.choidh.service.learning.entity.Learning;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.annotation.Rollback;

import static org.junit.jupiter.api.Assertions.*;

@Slf4j
@Rollback(value = false)
@RequiredArgsConstructor(onConstructor = @__(@Autowired))
class ProfessionalAccountRepositoryTest extends AbstractRepositoryTestConfig {
    private final ProfessionalAccountRepository professionalAccountRepository;

    @Test
    @DisplayName("ProfessionalAccount 단건 조회 By Account Id.")
    public void findByAccountId() throws Exception {
        Account testAccount = createAccount();
        createProfessionalAccount(testAccount);

        theLine();

        ProfessionalAccount professionalAccount = this.professionalAccountRepository.findByAccountId(testAccount.getId());

        assertNotNull(professionalAccount);
    }

    @Test
    @DisplayName("ProfessionalAccount 단건 조회 By Account Id. With Learning")
    public void findByAccountIdWithLearningList() throws Exception {
        Account testAccount = createAccount();
        ProfessionalAccount testProfessionalAccount = createProfessionalAccount(testAccount);

        for (int i = 0; i < 10; i++) {
            Learning learning = createLearning(testProfessionalAccount);
            testProfessionalAccount.setLearningList(learning);
        }

        Account testAccount2 = createAccount("sample2", "sample2@test.com");
        ProfessionalAccount testProfessionalAccount2 = createProfessionalAccount(testAccount2);

        for (int i = 0; i < 15; i++) {
            Learning learning = createLearning(testProfessionalAccount2);
            testProfessionalAccount2.setLearningList(learning);
        }

        theLine();

        ProfessionalAccount professionalAccount_1 = professionalAccountRepository.findByAccountIdWithLearningList(testAccount.getId());
        assertEquals(professionalAccount_1.getLearningList().size(), 10);

        ProfessionalAccount professionalAccount_2 = professionalAccountRepository.findByAccountIdWithLearningList(testAccount2.getId());
        assertEquals(professionalAccount_2.getLearningList().size(), 15);
    }
}
package com.choidh.service.purchaseHistory.repository;

import com.choidh.service.AbstractRepositoryTestConfig;
import com.choidh.service.account.entity.Account;
import com.choidh.service.professional.entity.ProfessionalAccount;
import com.choidh.service.learning.entity.Learning;
import com.choidh.service.purchaseHistory.entity.PurchaseHistory;
import com.choidh.service.purchaseHistory.vo.PurchaseStatus;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.annotation.Rollback;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

@Slf4j
@Rollback(value = false)
@RequiredArgsConstructor(onConstructor = @__(@Autowired))
class PurchaseHistoryRepositoryTest extends AbstractRepositoryTestConfig {
    private final PurchaseHistoryRepository purchaseHistoryRepository;

    @Test
    @DisplayName("구매이력 단건 조회. By Account Id and Learning Id")
    public void findByAccountIdAndLearningId() throws Exception {
        Account testAccount = createAccount();
        ProfessionalAccount professionalAccount = createProfessionalAccount(testAccount);
        Learning testLearning = createLearning(professionalAccount);

        Account account = createAccount("sample2", "sample2@test.com");

        purchaseHistoryRepository.save(PurchaseHistory.builder()
                .account(account)
                .learning(testLearning)
                .purchaseStatus(PurchaseStatus.BUY)
                .build());

        theLine();

        PurchaseHistory purchaseHistory = purchaseHistoryRepository.findByAccountIdAndLearningId(account.getId(), testLearning.getId());
        assertNotNull(purchaseHistory);
    }

    @Test
    @DisplayName("구매이력 목록 조회. By Account Id")
    public void findListByAccountId() throws Exception {
        Account testAccount = createAccount();
        ProfessionalAccount professionalAccount = createProfessionalAccount(testAccount);
        Account account = createAccount("sample2", "sample2@test.com");

        for (int i = 0; i < 10; i++) {
            Learning testLearning = createLearning(professionalAccount);
            purchaseHistoryRepository.save(PurchaseHistory.builder()
                    .account(account)
                    .learning(testLearning)
                    .purchaseStatus(PurchaseStatus.BUY)
                    .build());
        }

        theLine();

        List<PurchaseHistory> purchaseHistoryList = purchaseHistoryRepository.findListByAccountId(account.getId());
        assertFalse(purchaseHistoryList.isEmpty());
        assertEquals(purchaseHistoryList.size(), 10);
    }

}
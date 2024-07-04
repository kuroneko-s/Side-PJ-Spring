package com.choidh.service.purchaseHistory.service;

import com.choidh.service.account.entity.Account;
import com.choidh.service.account.service.AccountService;
import com.choidh.service.learning.entity.Learning;
import com.choidh.service.learning.service.LearningService;
import com.choidh.service.purchaseHistory.entity.PurchaseHistory;
import com.choidh.service.purchaseHistory.vo.PurchaseStatus;
import com.choidh.service.purchaseHistory.repository.PurchaseHistoryRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

/**
 * 구매이력 서비스
 */

@Slf4j
@Service
@Transactional(readOnly = true)
@RequiredArgsConstructor
public class PurchaseHistoryServiceImpl implements PurchaseHistoryService {
    private final AccountService accountService;
    private final LearningService learningService;
    private final PurchaseHistoryRepository purchaseHistoryRepository;

    /**
     * 강의 구매
     */
    @Override
    @Transactional
    public PurchaseHistory regPurchaseHistory(Long accountId, Long learningId) {
        Account account = accountService.getAccountById(accountId);
        Learning learning = learningService.getLearningById(learningId);

        PurchaseHistory purchaseHistory = purchaseHistoryRepository.findByAccountIdAndLearningId(accountId, learningId);

        if (purchaseHistory == null) {
            purchaseHistory = purchaseHistoryRepository.save(PurchaseHistory.builder()
                    .account(account)
                    .learning(learning)
                    .purchaseStatus(PurchaseStatus.BUY)
                    .build());

            account.getPurchaseHistories().add(purchaseHistory);
            learning.getPurchaseHistories().add(purchaseHistory);
        }

        return purchaseHistory;
    }

    /**
     * 강의 구매 취소
     */
    @Override
    @Transactional
    public void modPurchaseHistoryOfCancel(Long accountId, Long learningId) {
        Account account = accountService.getAccountById(accountId);
        Learning learning = learningService.getLearningById(learningId);

        PurchaseHistory purchaseHistory = purchaseHistoryRepository.findByAccountIdAndLearningId(account.getId(), learning.getId());

        if (purchaseHistory == null) {
            throw new IllegalArgumentException("The corresponding record does not exist. Account id - " + account.getId() + "Learning id - " + learning.getId());
        }

        purchaseHistory.setPurchaseStatus(PurchaseStatus.CANCEL);

        account.getPurchaseHistories().remove(purchaseHistory);
        learning.getPurchaseHistories().remove(purchaseHistory);
    }

    /**
     * 구매이력 목록조회 By accountId
     */
    @Override
    public List<PurchaseHistory> getPurchaseHistoryListOfAccountId(Long accountId) {
        return purchaseHistoryRepository.findListByAccountId(accountId);
    }
}

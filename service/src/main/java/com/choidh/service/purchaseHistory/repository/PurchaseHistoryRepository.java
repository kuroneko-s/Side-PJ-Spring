package com.choidh.service.purchaseHistory.repository;

import com.choidh.service.purchaseHistory.entity.PurchaseHistory;
import com.choidh.service.purchaseHistory.entity.PurchaseStatus;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.List;

public interface PurchaseHistoryRepository extends JpaRepository<PurchaseHistory, Long> {
    PurchaseHistory findByAccountIdAndLearningId(Long accountId, Long learningId);

    @Query(value = "select ph from PurchaseHistory ph where ph.account.id = :accountId")
    List<PurchaseHistory> findListByAccountId(Long accountId);
}

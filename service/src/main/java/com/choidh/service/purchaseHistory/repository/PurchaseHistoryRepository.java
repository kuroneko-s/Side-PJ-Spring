package com.choidh.service.purchaseHistory.repository;

import com.choidh.service.purchaseHistory.entity.PurchaseHistory;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.List;

public interface PurchaseHistoryRepository extends JpaRepository<PurchaseHistory, Long> {
    /**
     * 구매이력 단건 조회. By Account Id and Learning Id
     */
    @Query(value = "select ph " +
            "from PurchaseHistory ph " +
            "left join fetch ph.learning " +
            "left join fetch ph.account " +
            "where ph.learning.id = :learningId " +
            "and ph.account.id = :accountId")
    PurchaseHistory findByAccountIdAndLearningId(Long accountId, Long learningId);

    /**
     * 구매이력 목록 조회. By Account Id
     */
    @Query(value = "select ph " +
            "from PurchaseHistory ph " +
            "left join fetch ph.learning " +
            "left join fetch ph.account " +
            "where ph.account.id = :accountId")
    List<PurchaseHistory> findListByAccountId(Long accountId);
}

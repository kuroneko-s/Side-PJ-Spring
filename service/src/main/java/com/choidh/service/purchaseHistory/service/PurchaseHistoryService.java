package com.choidh.service.purchaseHistory.service;

import com.choidh.service.purchaseHistory.entity.PurchaseHistory;

import java.util.List;

/**
 * 구매이력 서비스
 */

public interface PurchaseHistoryService {
    /**
     * Reg 구매이력 추가
     */
    PurchaseHistory regPurchaseHistory(Long accountId, Long learningId);

    /**
     * Mod 강의 수정 Of 취소
     */
    void modPurchaseHistoryOfCancel(Long accountId, Long learningId);

    /**
     * 구매이력 목록조회 By accountId
     */
    List<PurchaseHistory> getPurchaseHistoryListOfAccountId(Long accountId);
}

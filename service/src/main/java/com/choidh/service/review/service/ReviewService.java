package com.choidh.service.review.service;


import com.choidh.service.account.entity.Account;
import com.choidh.service.review.vo.RegReviewVO;

public interface ReviewService {
    /**
     * 단건 리뷰 조회. By Account Id and Learning Id
     */
    boolean extReview(Long accountId, Long learningId);


    /**
     * Reg 리뷰 생성.
     */
    Account regReview(RegReviewVO regReviewVO, Long accountId, Long learningId);
}

package com.choidh.service.review.service;


import com.choidh.service.account.entity.Account;
import com.choidh.service.review.vo.RegReviewVO;

public interface ReviewService {

    /**
     * Reg 리뷰 생성.
     */
    Account regReview(RegReviewVO regReviewVO, Long accountId, Long learningId);
}

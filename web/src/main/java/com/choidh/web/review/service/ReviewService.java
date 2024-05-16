package com.choidh.web.review.service;


import com.choidh.service.account.entity.Account;
import com.choidh.service.review.entity.Review;
import com.choidh.web.review.vo.ReviewVO;

public interface ReviewService {
    /**
     * 리뷰 생성
     */
    Review createReview(ReviewVO reviewVO);

    /**
     * 리뷰 추가. (Account 및 Learning)
     */
    Account saveReview(ReviewVO reviewVO, Long accountId, Long learningId);
}

package com.choidh.web.review.service;


import com.choidh.service.account.entity.Account;
import com.choidh.service.account.repository.AccountRepository;
import com.choidh.service.learning.entity.Learning;
import com.choidh.service.learning.repository.LearningRepository;
import com.choidh.service.review.entity.Review;
import com.choidh.service.review.repository.ReviewRepository;
import com.choidh.web.review.vo.ReviewVO;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import static com.choidh.service.common.AppConstant.getAccountNotFoundErrorMessage;
import static com.choidh.service.common.AppConstant.getLearningNotFoundErrorMessage;

@Slf4j
@Service
@Transactional(readOnly = true)
@RequiredArgsConstructor(onConstructor = @__(@Autowired))
public class ReviewServiceImpl implements ReviewService{
    private final AccountRepository accountRepository;
    private final LearningRepository learningRepository;
    private final ReviewRepository reviewRepository;
    private final ModelMapper modelMapper;

    /**
     * 리뷰 생성
     */
    @Override
    @Transactional
    public Review createReview(ReviewVO reviewVO) {
        return reviewRepository.save(modelMapper.map(reviewVO, Review.class));
    }

    /**
     * 리뷰 추가. (Account 및 Learning)
     */
    @Override
    @Transactional
    public Account saveReview(ReviewVO reviewVO, Long accountId, Long learningId) {
        Learning learning = learningRepository.findById(learningId)
                .orElseThrow(() -> new IllegalArgumentException(getLearningNotFoundErrorMessage(learningId)));
        Account account = accountRepository.findById(accountId)
                .orElseThrow(() -> new IllegalArgumentException(getAccountNotFoundErrorMessage(accountId)));

        Review review = this.createReview(reviewVO);
        review.addReview(account, learning);

        return account;
    }
}

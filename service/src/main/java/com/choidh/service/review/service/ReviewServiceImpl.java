package com.choidh.service.review.service;


import com.choidh.service.account.entity.Account;
import com.choidh.service.account.service.AccountService;
import com.choidh.service.learning.entity.Learning;
import com.choidh.service.learning.service.LearningService;
import com.choidh.service.review.entity.Review;
import com.choidh.service.review.repository.ReviewRepository;
import com.choidh.service.review.vo.RegReviewVO;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Slf4j
@Service
@Transactional(readOnly = true)
@RequiredArgsConstructor(onConstructor = @__(@Autowired))
public class ReviewServiceImpl implements ReviewService{
    private final AccountService accountService;
    private final LearningService learningService;
    private final ReviewRepository reviewRepository;
    private final ModelMapper modelMapper;

    /**
     * 단건 리뷰 조회. By Account Id and Learning Id
     */
    @Override
    public boolean extReview(Long accountId, Long learningId) {
        Review review = reviewRepository.findByAccountIdAndLearningId(accountId, learningId);
        return review != null;
    }

    /**
     * Reg 리뷰 생성.
     */
    @Override
    @Transactional
    public Account regReview(RegReviewVO regReviewVO, Long accountId, Long learningId) {
        Learning learning = learningService.getLearningById(learningId);
        Account account = accountService.getAccountById(accountId);

        Review review = reviewRepository.save(modelMapper.map(regReviewVO, Review.class));
        account.setReviews(review);
        learning.setReviews(review);

        return account;
    }
}

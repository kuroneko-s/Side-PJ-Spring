package com.choidh.service.review.repository;


import com.choidh.service.review.entity.Review;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.transaction.annotation.Transactional;

@Transactional
public interface ReviewRepository extends JpaRepository<Review, Long> {
    @Query(value = "select r " +
            "from Review r " +
            "where r.account.id = :accountId " +
            "and r.learning.id = :learningId")
    Review findByAccountIdAndLearningId(Long accountId, Long learningId);
}

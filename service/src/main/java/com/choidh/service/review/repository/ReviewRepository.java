package com.choidh.service.review.repository;


import com.choidh.service.review.entity.Review;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.transaction.annotation.Transactional;

@Transactional
public interface ReviewRepository extends JpaRepository<Review, Long> {
}

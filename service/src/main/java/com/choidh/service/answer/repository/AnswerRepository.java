package com.choidh.service.answer.repository;

import com.choidh.service.answer.entity.Answer;
import org.springframework.data.jpa.repository.JpaRepository;

public interface AnswerRepository extends JpaRepository<Answer, Long> {
}

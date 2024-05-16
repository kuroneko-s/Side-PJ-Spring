package com.choidh.service.question.repository;


import com.choidh.service.question.entity.Question;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.transaction.annotation.Transactional;

@Transactional
public interface QuestionRepository extends JpaRepository<Question, Long> {

}

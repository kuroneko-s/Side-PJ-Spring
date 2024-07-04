package com.choidh.service.question.repository;


import com.choidh.service.question.entity.Question;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.transaction.annotation.Transactional;

public interface QuestionRepository extends JpaRepository<Question, Long> {
    @Query("select q " +
            "from Question q " +
            "where q.learning.id = :learningId " +
            "order by q.createdAt")
    Page<Question> findListByLearningId(Pageable pageable, Long learningId);

    @Query("select q " +
            "from Question q " +
            "where q.account.id = :accountId " +
            "order by q.createdAt")
    Page<Question> findListByAccountId(Pageable pageable, Long accountId);
}

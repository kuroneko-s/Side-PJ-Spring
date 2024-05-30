package com.choidh.service.account.repository;

import com.choidh.service.account.entity.ProfessionalAccount;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

public interface ProfessionalAccountRepository extends JpaRepository<ProfessionalAccount, Long> {
    // @Query(value = "select pa from ProfessionalAccount pa where pa.account.id = :accountId")
    ProfessionalAccount findByAccountId(Long accountId);

    @Query(value = "select pa from ProfessionalAccount pa join fetch pa.learningList where pa.account.id = :accountId")
    ProfessionalAccount findByAccountIdWithLearningList(Long accountId);
}

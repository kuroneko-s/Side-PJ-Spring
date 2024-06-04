package com.choidh.service.account.repository;

import com.choidh.service.account.entity.ProfessionalAccount;
import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

public interface ProfessionalAccountRepository extends JpaRepository<ProfessionalAccount, Long> {
    /**
     * ProfessionalAccount 단건 조회 By Account Id.
     */
    @Query(value = "select pa " +
            "from ProfessionalAccount pa " +
            "where pa.account.id = :accountId")
    @EntityGraph(attributePaths = {"account.cart"}, type = EntityGraph.EntityGraphType.LOAD)
    ProfessionalAccount findByAccountId(Long accountId);

    /**
     * ProfessionalAccount 단건 조회 By Account Id. With Learning
     */
    @Query(value = "select pa " +
            "from ProfessionalAccount pa " +
            "where pa.account.id = :accountId")
    @EntityGraph(attributePaths = {"account.cart", "learningList"}, type = EntityGraph.EntityGraphType.LOAD)
    ProfessionalAccount findByAccountIdWithLearningList(Long accountId);
}

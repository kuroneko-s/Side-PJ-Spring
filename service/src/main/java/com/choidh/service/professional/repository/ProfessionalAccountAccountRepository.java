package com.choidh.service.professional.repository;

import com.choidh.service.professional.entity.ProfessionalAccount;
import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.querydsl.QuerydslPredicateExecutor;

public interface ProfessionalAccountAccountRepository extends JpaRepository<ProfessionalAccount, Long>, QuerydslPredicateExecutor<ProfessionalAccount>, ProfessionalAccountRepositoryExtension {
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
            "left outer join pa.learningSet " +
            "left outer join pa.account.cart " +
            "where pa.account.id = :accountId")
    ProfessionalAccount findByAccountIdWithLearningList(Long accountId);

    @Query(value = "update from ProfessionalAccount p " +
            "set p.used = false " +
            "where p.id = :professionalId")
    @Modifying
    void delById(Long professionalId);

    @Query(value = "update from ProfessionalAccount p " +
            "set p.used = true " +
            "where p.id = :professionalId")
    @Modifying
    void modById(Long professionalId);
}

package com.choidh.service.professional.repository;

import com.choidh.service.professional.entity.ProfessionalAccount;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.querydsl.QuerydslPredicateExecutor;

public interface ProfessionalRepository extends JpaRepository<ProfessionalAccount, Long>, QuerydslPredicateExecutor<ProfessionalAccount>, ProfessionalRepositoryExtension {
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

package com.choidh.service.professional.repository;

import com.choidh.service.professional.entity.ProfessionalAccount;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.transaction.annotation.Transactional;

@Transactional
public interface ProfessionalRepositoryExtension {
    /**
     * 강의 제공자 페이징
     */
    Page<ProfessionalAccount> findByList(Pageable pageable);
}

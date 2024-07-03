package com.choidh.service.professional.service;

import com.choidh.service.professional.vo.ProfessionalListResult;
import org.springframework.data.domain.Pageable;

public interface ProfessionalService {
    /**
     * 강의 제공자 페이징
     */
    ProfessionalListResult getProfessionalList(Pageable pageable);

    /**
     * 강의 제공자 비활성화
     */
    void delProfessionalById(Long professionalId);

    /**
     * 강의 제공자 활성화
     */
    void modProfessionalById(Long professionalId);
}

package com.choidh.service.professional.service;

import com.choidh.service.professional.entity.ProfessionalAccount;
import com.choidh.service.professional.vo.ProfessionalListResult;
import com.choidh.service.professional.vo.RegProfessionalAccountVO;
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

    /**
     * 강의 제공자 단건 조회. By Account Id
     */
    ProfessionalAccount getProfessionalByAccountId(Long accountId);

    /**
     * 강의 제공자 생성
     */
    ProfessionalAccount regProfessionalAccount(RegProfessionalAccountVO regProfessionalAccountVO);
}

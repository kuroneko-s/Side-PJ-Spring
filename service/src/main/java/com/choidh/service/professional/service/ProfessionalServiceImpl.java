package com.choidh.service.professional.service;

import com.choidh.service.common.pagination.Paging;
import com.choidh.service.learning.service.LearningService;
import com.choidh.service.professional.entity.ProfessionalAccount;
import com.choidh.service.professional.repository.ProfessionalAccountAccountRepository;
import com.choidh.service.professional.vo.ProfessionalListResult;
import com.choidh.service.professional.vo.RegProfessionalAccountVO;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.HashSet;

@Service
public class ProfessionalServiceImpl implements ProfessionalService {
    @Autowired
    private ProfessionalAccountAccountRepository professionalAccountRepository;
    @Autowired
    private LearningService learningService;

    /**
     * 강의 제공자 페이징
     */
    @Override
    public ProfessionalListResult getProfessionalList(Pageable pageable) {
        Page<ProfessionalAccount> professionalAccountPage = professionalAccountRepository.findByList(pageable);

        String paginationUrl = "/admin/professional/list?sort=createdAt,asc&page=";
        for (Sort.Order order : pageable.getSort()) {
            Sort.Direction direction = order.getDirection();

            if (direction.isDescending()) {
                paginationUrl = "/admin/professional/list?sort=createdAt,desc&page=";
            }
        }

        Paging paging = Paging.builder()
                .hasNext(professionalAccountPage.hasNext())
                .hasPrevious(professionalAccountPage.hasPrevious())
                .number(professionalAccountPage.getNumber())
                .totalPages(Math.max(professionalAccountPage.getTotalPages() - 1, 0))
                .paginationUrl(paginationUrl)
                .build();

        return ProfessionalListResult.builder()
                .professionalAccountList(professionalAccountPage.getContent())
                .paging(paging)
                .build();
    }

    /**
     * 강의 제공자 비활성화
     */
    @Override
    @Transactional
    public void delProfessionalById(Long professionalId) {
        // 강의 제공자의 강의들을 모두 비활성화
        learningService.delLearningByProfessionalId(professionalId);

        professionalAccountRepository.delById(professionalId);
    }

    /**
     * 강의 제공자 활성화
     */
    @Override
    public void modProfessionalById(Long professionalId) {
        // 강의 제공자의 강의들을 모두 활성화
        learningService.modLearningByProfessionalId(professionalId);

        professionalAccountRepository.modById(professionalId);
    }

    /**
     * 강의 제공자 단건 조회. By Account Id
     */
    @Override
    public ProfessionalAccount getProfessionalByAccountId(Long accountId) {
        ProfessionalAccount professionalAccount = professionalAccountRepository.findByAccountId(accountId);

        if (professionalAccount == null) {
            throw new IllegalArgumentException(accountId + "에 해당하는 강의 제공자를 찾을 수 없습니다.");
        }

        return professionalAccount;
    }

    /**
     * 강의 제공자 생성
     */
    @Override
    public ProfessionalAccount regProfessionalAccount(RegProfessionalAccountVO regProfessionalAccountVO) {
        return professionalAccountRepository.save(ProfessionalAccount.builder()
                .account(regProfessionalAccountVO.getAccount())
                .name(regProfessionalAccountVO.getName())
                .history(regProfessionalAccountVO.getHistory())
                .description(regProfessionalAccountVO.getDescription())
                .learningSet(new HashSet<>())
                .used(false)
                .build());
    }
}

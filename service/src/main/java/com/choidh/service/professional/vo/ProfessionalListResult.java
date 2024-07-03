package com.choidh.service.professional.vo;

import com.choidh.service.common.pagination.Paging;
import com.choidh.service.professional.entity.ProfessionalAccount;
import lombok.*;

import java.util.List;

@Getter @Setter @Builder
@NoArgsConstructor @AllArgsConstructor
public class ProfessionalListResult {
    private List<ProfessionalAccount> professionalAccountList;

    private Paging paging;
}

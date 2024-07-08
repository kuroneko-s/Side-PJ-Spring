package com.choidh.service.professional.vo;

import com.choidh.service.account.entity.Account;
import lombok.*;

@Getter @Setter @Builder
@NoArgsConstructor @AllArgsConstructor
public class RegProfessionalAccountVO {
    private Account account;

    private String name;

    private String description;

    private String history;
}

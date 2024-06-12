package com.choidh.service.learning.vo;

import com.choidh.service.account.entity.Account;
import com.choidh.service.learning.entity.Learning;
import lombok.*;

@Getter @Setter @Builder
@NoArgsConstructor
@AllArgsConstructor
public class LearningDetailVO {
    private Account account;

    private Learning learning;

}

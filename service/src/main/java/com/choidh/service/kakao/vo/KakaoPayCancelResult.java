package com.choidh.service.kakao.vo;

import com.choidh.service.account.entity.Account;
import com.choidh.service.learning.entity.Learning;
import lombok.*;

import java.util.List;
import java.util.Map;

@Getter @Setter @Builder
@NoArgsConstructor
@AllArgsConstructor
public class KakaoPayCancelResult {
    private List<Learning> learningList;

    private Account account;

    private Map<Long, List<String>> learningImageMap;
}

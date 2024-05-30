package com.choidh.service.kakao.vo;

import com.choidh.service.account.entity.Account;
import com.choidh.service.learning.entity.Learning;
import lombok.*;

import java.util.List;

@Getter @Setter @Builder
@NoArgsConstructor
@AllArgsConstructor
public class KakaoPayCancelResult {
    private KakaoPayCancelVO kakaoPayCancelVO;
    private List<Learning> learningList;
    private Account account;
}

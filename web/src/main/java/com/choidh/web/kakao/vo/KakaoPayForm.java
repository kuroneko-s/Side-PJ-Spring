package com.choidh.web.kakao.vo;

import com.choidh.service.annotation.Name;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter @Setter
@NoArgsConstructor
@AllArgsConstructor
public class KakaoPayForm {
    @Name(name = "강의 ID", description = "구분 값 - ,")
    private String learningIds;
}

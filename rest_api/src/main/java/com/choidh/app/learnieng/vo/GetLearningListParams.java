package com.choidh.app.learnieng.vo;

import com.choidh.service.common.annotation.Name;
import lombok.*;

@Getter @Setter
@NoArgsConstructor @AllArgsConstructor
@Builder
public class GetLearningListParams {
    @Name(name = "메인 카테고리")
    private String mainCategory;

    @Name(name = "서브 카테고리")
    private String subCategory;
}

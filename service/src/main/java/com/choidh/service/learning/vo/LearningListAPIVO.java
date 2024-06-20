package com.choidh.service.learning.vo;

import com.choidh.service.common.pagination.Paging;
import lombok.*;

import java.util.List;

@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class LearningListAPIVO {
    private List<LearningAPIVO> learningList;
    private Paging paging;
}

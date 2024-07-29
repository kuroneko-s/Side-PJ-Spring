package com.choidh.service.learning.vo.web;

import com.choidh.service.common.pagination.Paging;
import com.choidh.service.learning.entity.Learning;
import lombok.*;
import org.springframework.data.domain.Page;

import java.util.List;

@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class LearningListAPIVO {
    private List<LearningAPIVO> learningList;
    private Paging paging;
    private Page<Learning> learningPage;
}

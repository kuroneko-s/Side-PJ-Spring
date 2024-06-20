package com.choidh.service.learning.vo;

import com.choidh.service.annotation.Name;
import com.choidh.service.common.pagination.Paging;
import com.choidh.service.learning.entity.Learning;
import lombok.*;
import org.springframework.data.domain.Page;

import java.util.List;
import java.util.Map;

@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class LearningListVO {
    @Name(name = "Learning 페이징 목록")
    private Page<Learning> learningPage;

    @Name(name = "Learning 이미지 경로 정보")
    private Map<Long, List<String>> learningImageMap;

    private Paging paging;
}

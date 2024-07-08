package com.choidh.service.learning.vo;

import com.choidh.service.common.annotation.Name;
import com.choidh.service.common.pagination.Paging;
import com.choidh.service.learning.entity.Learning;
import lombok.*;

import java.util.List;
import java.util.Map;

@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class LearningListVO {
    @Name(name = "Learning 페이징 목록")
    private List<Learning> learningList;

    @Name(name = "Learning 이미지 경로 정보")
    private Map<Long, List<String>> learningImageMap;

    @Name(name = "Learning 이미지 등록 유무")
    private Map<Long, Boolean> imageUploadMap;

    private Paging paging;
}

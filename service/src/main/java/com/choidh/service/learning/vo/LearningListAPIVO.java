package com.choidh.service.learning.vo;

import com.choidh.service.annotation.Name;
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
public class LearningListAPIVO {
    private List<LearningAPIVO> learningList;

    @Name(name = "페이지네이션 기본 버튼 Url")
    private String paginationUrl;

    @Name(name = "이전 버튼 활성화 유무")
    private boolean hasPrevious;

    @Name(name = "다음 버튼 활성화 유무")
    private boolean hasNext;

    @Name(name = "현재 페이지")
    private int number;

    @Name(name = "총 페이지")
    private int totalPages;
}

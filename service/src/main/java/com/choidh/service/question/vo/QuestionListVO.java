package com.choidh.service.question.vo;

import com.choidh.service.common.pagination.Paging;
import com.choidh.service.question.entity.Question;
import lombok.*;
import org.springframework.data.domain.Page;

@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class QuestionListVO {
    private Page<Question> questionPage;
    private Paging paging;
}

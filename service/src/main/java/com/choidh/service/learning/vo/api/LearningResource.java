package com.choidh.service.learning.vo.api;

import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.Getter;
import org.springframework.hateoas.EntityModel;
import org.springframework.hateoas.Link;

@Getter
@JsonInclude(JsonInclude.Include.NON_NULL)
public class LearningResource extends EntityModel<LearningVO> {
    public LearningResource(LearningVO learningVO, Link... links) {
        super(learningVO, links);
    }
}

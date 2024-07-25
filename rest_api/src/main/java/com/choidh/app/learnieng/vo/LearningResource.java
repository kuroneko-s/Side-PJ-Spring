package com.choidh.app.learnieng.vo;

import com.choidh.app.learnieng.LearningController;
import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.Getter;
import org.springframework.hateoas.EntityModel;
import org.springframework.hateoas.Link;

import static org.springframework.hateoas.server.mvc.WebMvcLinkBuilder.linkTo;

@Getter @JsonInclude(JsonInclude.Include.NON_NULL)
public class LearningResource extends EntityModel<LearningVOList> {
    public LearningResource(LearningVOList learningVOList, Link... links) {
        super(learningVOList, links);
        add(linkTo(LearningController.class).withSelfRel());
    }
}

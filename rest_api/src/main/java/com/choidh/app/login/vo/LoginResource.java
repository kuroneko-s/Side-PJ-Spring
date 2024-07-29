package com.choidh.app.login.vo;

import com.choidh.app.account.AccountController;
import com.choidh.app.learnieng.LearningController;
import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.Getter;
import org.springframework.hateoas.EntityModel;
import org.springframework.hateoas.Link;

import static org.springframework.hateoas.server.mvc.WebMvcLinkBuilder.linkTo;

@Getter
@JsonInclude(JsonInclude.Include.NON_NULL)
public class LoginResource extends EntityModel<LoginResponse> {
    public LoginResource(LoginResponse content, Long accountId) {
        super(content);
        linkTo(LearningController.class).slash("list").withRel("강의 목록 조회");
        linkTo(AccountController.class).slash(accountId).withRel("계정 정보 조회");
    }

    public LoginResource(LoginResponse content, Link... links) {
        super(content, links);
    }
}

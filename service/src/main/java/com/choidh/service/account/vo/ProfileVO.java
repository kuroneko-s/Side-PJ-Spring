package com.choidh.service.account.vo;

import com.choidh.service.account.entity.Account;
import com.choidh.service.learning.entity.Learning;
import com.choidh.service.question.entity.Question;
import lombok.Builder;
import lombok.Getter;

import java.util.List;

@Builder
@Getter
public class ProfileVO {
    private Account account;

    private List<String> tagTitleList;

    private List<String> learningTitleList;

    private List<Learning> learningTop4List;

    private List<Question> questionList;
}

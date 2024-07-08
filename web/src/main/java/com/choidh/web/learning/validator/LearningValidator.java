package com.choidh.web.learning.validator;

import com.choidh.service.common.annotation.IsValidator;
import com.choidh.web.learning.vo.LearningFormVO;
import org.springframework.validation.Errors;
import org.springframework.validation.Validator;

@IsValidator
public class LearningValidator implements Validator {
    @Override
    public boolean supports(Class<?> aClass) {
        return aClass.isAssignableFrom(LearningFormVO.class);
    }

    @Override
    public void validate(Object o, Errors errors) {
        LearningFormVO learningFormVO = (LearningFormVO) o;

        if (learningFormVO.getTitle() == null || learningFormVO.getTitle().isEmpty()) {
            errors.rejectValue("learningTitle", "wrong.learningTitle", "값을 입력해주세요.");
        }

        if (learningFormVO.getSubscription() == null || learningFormVO.getSubscription().isEmpty()) {
            errors.rejectValue("subscription", "wrong.subscription", "설명 미입력");
        }

        if (learningFormVO.getSimpleSubscription() == null || learningFormVO.getSimpleSubscription().isEmpty()) {
            errors.rejectValue("simpleSubscription", "wrong.simplesubscription", "간단 설명 미입력");
        }

        if (learningFormVO.getMainCategory() == null || learningFormVO.getMainCategory().isEmpty()) {
            errors.rejectValue("mainCategory", "wrong.mainCategory", "메인 카테고리 미설정");
        }

        if (learningFormVO.getSkills() == null || learningFormVO.getSkills().isEmpty()) {
            errors.rejectValue("skills", "wrong.skills", "분야 미설정");
        }

    }
}

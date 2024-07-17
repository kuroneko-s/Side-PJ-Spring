package com.choidh.web.professional.validator;

import com.choidh.service.common.annotation.IsValidator;
import com.choidh.service.common.utiles.StringUtils;
import com.choidh.web.professional.vo.LearningFormVO;
import org.springframework.validation.Errors;
import org.springframework.validation.Validator;

import static com.choidh.service.common.utiles.StringUtils.isNullOrEmpty;

@IsValidator
public class LearningValidator implements Validator {
    @Override
    public boolean supports(Class<?> aClass) {
        return aClass.isAssignableFrom(LearningFormVO.class);
    }

    @Override
    public void validate(Object o, Errors errors) {
        LearningFormVO learningFormVO = (LearningFormVO) o;

        if (isNullOrEmpty(learningFormVO.getTitle())) {
            errors.rejectValue("title", "wrong.title", "강의 제목을 입력해주세요.");
        }

        if (isNullOrEmpty(learningFormVO.getSubscription())) {
            errors.rejectValue("subscription", "wrong.subscription", "강의에 대한 설명을 입력해주세요.");
        }

        if (isNullOrEmpty(learningFormVO.getSimpleSubscription())) {
            errors.rejectValue("simpleSubscription", "wrong.simplesubscription", "강의에 대한 간단 설명을 입력해주세요.");
        }

        if (isNullOrEmpty(learningFormVO.getMainCategory())) {
            errors.rejectValue("mainCategory", "wrong.mainCategory", "강의에 대한 메인 카테고리를 설정해주세요.");
        }

        if (isNullOrEmpty(learningFormVO.getSkills())) {
            errors.rejectValue("skills", "wrong.skills", "강의에 대한 분야를 설정해주세요.");
        }

    }
}

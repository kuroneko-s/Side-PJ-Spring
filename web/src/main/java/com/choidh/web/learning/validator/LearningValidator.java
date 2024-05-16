package com.choidh.web.learning.validator;

import com.choidh.web.common.annotation.IsValidator;
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
        if(learningFormVO.getTitle() == null || learningFormVO.getTitle().isEmpty()){
            errors.rejectValue("title", "wrong.title", "값을 입력해주세요.");
        }
        if(learningFormVO.getLecturerName() == null || learningFormVO.getLecturerName().isEmpty()){
            errors.rejectValue("lecturerName", "wrong.lecturerName", "이름 미입력");
        }
        if(learningFormVO.getSimplesubscription() == null || learningFormVO.getSimplesubscription().isEmpty()){
            errors.rejectValue("simplesubscription", "wrong.simplesubscription", "간단 설명 미입력");
        }

        if(learningFormVO.getSubscription() == null || learningFormVO.getSubscription().isEmpty()){
            errors.rejectValue("subscription", "wrong.subscription", "설명 미입력");
        }
        if(learningFormVO.getLecturerDescription() == null || learningFormVO.getLecturerDescription().isEmpty()){
            errors.rejectValue("lecturerDescription", "wrong.lecturerDescription", "설명 미입력");
        }
        if (learningFormVO.getKategorie() == null || learningFormVO.getKategorie().isEmpty()) {
            errors.rejectValue("kategorie", "wrong.kategorie", "카테고리 미설정");
        }
    }
}

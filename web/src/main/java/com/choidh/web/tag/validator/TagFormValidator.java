package com.choidh.web.tag.validator;

import com.choidh.service.excel.validator.IsValidator;
import com.choidh.web.tag.vo.RegTagForm;
import org.springframework.validation.Errors;
import org.springframework.validation.Validator;

@IsValidator
public class TagFormValidator implements Validator {
    @Override
    public boolean supports(Class<?> aClass) {
        return aClass.isAssignableFrom(RegTagForm.class);
    }

    @Override
    public void validate(Object target, Errors errors) {
        RegTagForm regTagForm = (RegTagForm) target;

        if (regTagForm.getTitle() == null || regTagForm.getTitle().isEmpty()) {
            errors.rejectValue("title", "wrong.title", new Object[]{regTagForm.getTitle()}, "태그를 입력해주세요.");
        }

    }
}

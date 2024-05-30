package com.choidh.web.tag.validator;

import com.choidh.service.excel.validator.IsValidator;
import com.choidh.web.tag.vo.TagForm;
import org.springframework.validation.Errors;
import org.springframework.validation.Validator;

@IsValidator
public class TagFormValidator implements Validator {
    @Override
    public boolean supports(Class<?> aClass) {
        return aClass.isAssignableFrom(TagForm.class);
    }

    @Override
    public void validate(Object target, Errors errors) {
        TagForm tagForm = (TagForm) target;

        if (tagForm.getTitle() == null || tagForm.getTitle().isEmpty()) {
            errors.rejectValue("title", "wrong.title", new Object[]{tagForm.getTitle()}, "태그를 입력해주세요.");
        }

    }
}

package com.choidh.web.mail.validator;

import com.choidh.service.excel.validator.IsValidator;
import com.choidh.web.mail.vo.EmailFormVO;
import org.springframework.validation.Errors;
import org.springframework.validation.Validator;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

@IsValidator
public class EmailValidator implements Validator {
    private final String EMAIL_REGEX = "^[a-zA-Z0-9._%+-]+@[a-zA-Z0-9.-]+\\.[a-zA-Z]{2,}$";
    private final Pattern EMAIL_PATTERN = Pattern.compile(EMAIL_REGEX);

    @Override
    public boolean supports(Class<?> aClass) {
        return aClass.isAssignableFrom(EmailFormVO.class);
    }

    @Override
    public void validate(Object target, Errors errors) {
        EmailFormVO emailFormVO = (EmailFormVO) target;
        Matcher matcher = EMAIL_PATTERN.matcher(emailFormVO.getEmail());

        if (!matcher.matches()) {
            errors.rejectValue("email", "wrong.email", new Object[]{emailFormVO.getEmail()}, "메일 형식에 알맞지 않습니다.");
        }
    }
}

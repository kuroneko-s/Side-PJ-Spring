package com.choidh.service.excel.validator;

import com.choidh.service.excel.model.ExcelVO;
import org.apache.logging.log4j.util.Strings;
import org.springframework.validation.Errors;
import org.springframework.validation.Validator;

@IsValidator
public class ExcelValidator implements Validator {
    @Override
    public boolean supports(Class<?> clazz) {
        return clazz.isAssignableFrom(ExcelVO.class);
    }

    @Override
    public void validate(Object target, Errors errors) {
        ExcelVO excelVO = (ExcelVO) target;

        if (Strings.isBlank(excelVO.getSqlSelectName())) {
            errors.rejectValue("sqlSelectName", "is null", "값을 좀 넣어라.");
        }
    }
}

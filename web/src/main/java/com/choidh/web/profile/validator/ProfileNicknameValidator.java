package com.choidh.web.profile.validator;

import com.choidh.service.account.repository.AccountRepository;
import com.choidh.service.excel.validator.IsValidator;
import com.choidh.web.profile.vo.ProfileUpdateForm;
import lombok.RequiredArgsConstructor;
import org.springframework.validation.Errors;
import org.springframework.validation.Validator;

@IsValidator
@RequiredArgsConstructor
public class ProfileNicknameValidator implements Validator {
    private final AccountRepository accountRepository;

    @Override
    public boolean supports(Class<?> clazz) {
        return clazz.isAssignableFrom(ProfileUpdateForm.class);
    }

    @Override
    public void validate(Object target, Errors errors) {
        ProfileUpdateForm profileUpdateForm = (ProfileUpdateForm) target;

        if (accountRepository.existsByNickname(profileUpdateForm.getNickname()))
            errors.rejectValue("nickname", "wrong.nickname", "닉네임이 중복입니다.");

        if (profileUpdateForm.getDescription() == null || profileUpdateForm.getDescription().isEmpty())
            errors.rejectValue("description", "wrong.description", "자기 소개를 입력해주세요.");
    }
}

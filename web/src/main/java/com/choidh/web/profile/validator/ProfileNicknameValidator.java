package com.choidh.web.profile.validator;

import com.choidh.service.account.repository.AccountRepository;
import com.choidh.service.excel.validator.IsValidator;
import com.choidh.web.profile.vo.ProfileUpdateVO;
import lombok.RequiredArgsConstructor;
import org.springframework.validation.Errors;
import org.springframework.validation.Validator;

@IsValidator
@RequiredArgsConstructor
public class ProfileNicknameValidator implements Validator {
    private final AccountRepository accountRepository;

    @Override
    public boolean supports(Class<?> clazz) {
        return clazz.isAssignableFrom(ProfileUpdateVO.class);
    }

    @Override
    public void validate(Object target, Errors errors) {
        ProfileUpdateVO profileUpdateVO = (ProfileUpdateVO) target;

        if (accountRepository.existsByNickname(profileUpdateVO.getNickname()))
            errors.rejectValue("nickname", "wrong.nickname", "닉네임이 중복입니다.");

        if (profileUpdateVO.getDescription() == null || profileUpdateVO.getDescription().isEmpty())
            errors.rejectValue("description", "wrong.description", "자기 소개를 입력해주세요.");
    }
}

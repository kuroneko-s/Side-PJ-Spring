package com.choidh.web.profile.validator;

import com.choidh.service.account.entity.Account;
import com.choidh.service.account.repository.AccountRepository;
import com.choidh.web.common.annotation.IsValidator;
import com.choidh.web.profile.vo.PasswordUpdateForm;
import lombok.RequiredArgsConstructor;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.validation.Errors;
import org.springframework.validation.Validator;

@IsValidator
@RequiredArgsConstructor
public class ProfilePasswordValidator implements Validator {
    private final AccountRepository accountRepository;
    private final PasswordEncoder passwordEncoder;

    @Override
    public boolean supports(Class<?> clazz) {
        return clazz.isAssignableFrom(PasswordUpdateForm.class);
    }

    @Override
    public void validate(Object target, Errors errors) {
        PasswordUpdateForm form = (PasswordUpdateForm) target;
        Account account = accountRepository.findByNicknameAndTokenChecked(form.getAccountNickname(), true);

        if (!passwordEncoder.matches(form.getNowPassword(), account.getPassword())) {
            errors.rejectValue("nowPassword", "wrong.nowPassword", "패스워드가 일치하지 않습니다.");
        }

        if (!form.checkPassword()) {
            errors.rejectValue("newPassword", "wrong.newPassword", "패스워드가 일치하지 않습니다.");
        }
    }
}

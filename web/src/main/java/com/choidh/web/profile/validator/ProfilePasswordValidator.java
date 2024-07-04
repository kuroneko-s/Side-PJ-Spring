package com.choidh.web.profile.validator;

import com.choidh.service.account.entity.Account;
import com.choidh.service.account.repository.AccountRepository;
import com.choidh.service.common.annotation.IsValidator;
import com.choidh.web.profile.vo.PasswordUpdateVO;
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
        return clazz.isAssignableFrom(PasswordUpdateVO.class);
    }

    @Override
    public void validate(Object target, Errors errors) {
        PasswordUpdateVO form = (PasswordUpdateVO) target;
        Account account = accountRepository.findByIdAndChecked(Long.valueOf(form.getAccountId()), true);

        if (!passwordEncoder.matches(form.getNowPassword(), account.getPassword())) {
            errors.rejectValue("nowPassword", "wrong.nowPassword", "패스워드가 일치하지 않습니다.");
        }

        if (!form.checkPassword()) {
            errors.rejectValue("newPassword", "wrong.newPassword", "패스워드가 일치하지 않습니다.");
        }
    }
}

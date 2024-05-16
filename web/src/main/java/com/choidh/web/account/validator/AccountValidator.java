package com.choidh.web.account.validator;


import com.choidh.service.account.repository.AccountRepository;
import com.choidh.web.common.annotation.IsValidator;
import com.choidh.web.account.controller.AccountController;
import com.choidh.web.account.vo.AccountVO;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.validation.Errors;
import org.springframework.validation.Validator;

/**
 * {@link AccountController}의 Validator
 */
@IsValidator
@RequiredArgsConstructor(onConstructor = @__(@Autowired))
public class AccountValidator implements Validator {
    private final AccountRepository accountRepository;

    @Override
    public boolean supports(Class<?> clazz) {
        return clazz.isAssignableFrom(AccountVO.class);
    }

    @Override
    public void validate(Object target, Errors errors) {
        AccountVO accountServiceVO = (AccountVO) target;

        if (accountRepository.existsByEmail(accountServiceVO.getEmail()))
            errors.rejectValue("email", "wrong.email", new Object[]{accountServiceVO.getEmail()}, "이 이메일은 사용하실 수 없습니다.");

        if (accountRepository.existsByNickname(accountServiceVO.getNickname()))
            errors.rejectValue("nickname", "wrong.nickname", new Object[]{accountServiceVO.getNickname()}, "이 닉네임은 사용하실 수 없습니다.");

        if (!accountServiceVO.checkingPassword())
            errors.rejectValue("password", "wrong.password", new Object[]{accountServiceVO.getPassword()}, "패스워드가 일치하지 않습니다.");

    }
}

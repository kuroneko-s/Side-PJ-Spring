package com.choidh.web.account.validator;


import com.choidh.service.account.repository.AccountRepository;
import com.choidh.service.common.annotation.IsValidator;
import com.choidh.web.account.vo.AccountVO;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.validation.Errors;
import org.springframework.validation.Validator;

/**
 * {@link AccountVO}의 Validator
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
        AccountVO accountVO = (AccountVO) target;

        if (accountRepository.existsByEmail(accountVO.getEmail()))
            errors.rejectValue("email", "wrong.email", new Object[]{accountVO.getEmail()}, "이 이메일은 사용하실 수 없습니다.");

        if (accountRepository.existsByNickname(accountVO.getNickname()))
            errors.rejectValue("nickname", "wrong.nickname", new Object[]{accountVO.getNickname()}, "이 닉네임은 사용하실 수 없습니다.");

        if (!accountVO.checkingPassword())
            errors.rejectValue("password", "wrong.password", new Object[]{accountVO.getPassword()}, "패스워드가 일치하지 않습니다.");

    }
}

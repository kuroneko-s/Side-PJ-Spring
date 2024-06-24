package com.choidh.web.profile.vo;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.hibernate.validator.constraints.Length;

import javax.validation.constraints.NotBlank;

@Getter @Setter
@NoArgsConstructor
@AllArgsConstructor
public class PasswordUpdateVO {
    @NotBlank
    @Length(min = 10, max = 50, message = "패스워드 길이가 알맞지 않습니다. 다시 입력해주세요")
    private String nowPassword;
    @NotBlank
    @Length(min = 10, max = 50, message = "패스워드 길이가 알맞지 않습니다. 다시 입력해주세요")
    private String newPassword;
    @Length(min = 10, max = 50, message = "패스워드 길이가 알맞지 않습니다. 다시 입력해주세요")
    @NotBlank
    private String newPasswordCheck;

    @NotBlank
    private String accountNickname;

    public boolean checkPassword() {
        return newPassword.equals(newPasswordCheck);
    }
}

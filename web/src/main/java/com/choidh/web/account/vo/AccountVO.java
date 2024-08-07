package com.choidh.web.account.vo;

import lombok.*;
import org.hibernate.validator.constraints.Length;

import javax.validation.constraints.Email;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.Pattern;

@Getter @Setter @Builder
@NoArgsConstructor
@AllArgsConstructor
public class AccountVO {

    @NotBlank
    @Length(min = 3, max = 20)
    @Pattern(regexp = "^[ㄱ-ㅎ가-힣a-zA-Z0-9_!@#$%-]{3,20}$", message = "다시 입력해주세요. 3자이상이여야 합니다.")
    private String nickname;

    @NotBlank
    @Email
    private String email;

    @NotBlank
    @Length(min = 10, max = 50)
    private String password;

    @NotBlank
    @Length(min = 10, max = 50)
    private String passwordcheck;

    public boolean checkingPassword() {
        return this.password.equals(passwordcheck);
    }

}

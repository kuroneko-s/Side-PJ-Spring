package com.choidh.web.profile.vo;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.hibernate.validator.constraints.Length;

import javax.validation.constraints.Pattern;

@Getter @Setter
@NoArgsConstructor
@AllArgsConstructor
public class ProfileUpdateVO {
    @Length(max = 20)
    @Pattern(regexp = "^[ㄱ-ㅎ가-힣a-z0-9_!@#$%-]{2,20}$")
    private String nickname;

    private String description;
}

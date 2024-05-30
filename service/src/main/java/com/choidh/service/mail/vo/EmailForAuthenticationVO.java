package com.choidh.service.mail.vo;


import com.choidh.service.account.entity.Account;
import lombok.*;

@Getter @Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class EmailForAuthenticationVO {
    private String nickname;

    private String emailAuthenticationToken;

    private String email;

    public static EmailForAuthenticationVO getInstance(Account account) {
        return EmailForAuthenticationVO.builder()
                .nickname(account.getNickname())
                .email(account.getEmail())
                .emailAuthenticationToken(account.getTokenForEmailForAuthentication())
                .build();
    }
}

package com.choidh.service.mail.vo;


import com.choidh.service.account.entity.Account;
import lombok.*;

@Getter @Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class EmailCheckMessageVO {
    private String nickname;

    private String emailCheckToken;

    private String email;

    public static EmailCheckMessageVO getInstance(Account account) {
        return EmailCheckMessageVO.builder()
                .nickname(account.getNickname())
                .email(account.getEmail())
                .emailCheckToken(account.getEmailCheckToken())
                .build();
    }
}

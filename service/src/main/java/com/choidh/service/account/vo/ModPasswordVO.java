package com.choidh.service.account.vo;

import lombok.*;

@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ModPasswordVO {
    private String nowPassword;
    private String newPassword;
    private String newPasswordCheck;
    private String accountNickname;
}

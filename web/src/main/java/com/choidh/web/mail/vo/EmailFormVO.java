package com.choidh.web.mail.vo;

import lombok.*;

import javax.validation.constraints.Email;
import javax.validation.constraints.NotBlank;

@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class EmailFormVO {
    @NotBlank
    @Email
    private String email;
}

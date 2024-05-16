package com.choidh.web.account.vo;

import lombok.Data;

import javax.validation.constraints.Email;
import javax.validation.constraints.NotBlank;

@Data
public class EmailTokenVO {

    @NotBlank
    @Email
    private String email;

}

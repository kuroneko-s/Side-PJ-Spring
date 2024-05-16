package com.choidh.service.mail.vo;


import lombok.*;

@Getter @Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class EmailMessageVO {
    private String to;

    private String subject;

    private String message;
}

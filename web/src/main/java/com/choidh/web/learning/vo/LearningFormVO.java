package com.choidh.web.learning.vo;

import lombok.*;
import org.hibernate.validator.constraints.Length;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;

@Getter @Setter @Builder
@NoArgsConstructor
@AllArgsConstructor
public class LearningFormVO {
    @NotBlank
    @NotNull
    @Length(max = 255)
    private String title;

    @NotNull
    private int price;

    @NotBlank @NotNull
    private String subscription;

    @NotBlank @NotNull
    private String simpleSubscription;

    @NotNull @NotBlank
    private String mainCategory;

    @NotNull
    private String skills;
}

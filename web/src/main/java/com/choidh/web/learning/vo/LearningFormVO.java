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

    @NotBlank @NotNull
    private String simplesubscription;

    @NotBlank @NotNull
    private String subscription;

    @NotNull @NotBlank
    private String lecturerName;

    @NotNull @NotBlank
    private String lecturerDescription;

    @NotNull
    private int price;

    @NotNull
    private String kategorie;
}

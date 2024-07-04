package com.choidh.service.learning.vo;

import com.choidh.service.common.annotation.Name;
import lombok.*;

import java.time.LocalDateTime;
import java.util.Set;

@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class LearningAPIVO {
    @Name(name = "Learning PK")
    private Long learningId;

    @Name(name = "배너 이미지 경로")
    private String imageSrc;

    @Name(name = "배너 이미지 이름")
    private String imageName;

    @Name(name = "강의 명")
    private String title;

    @Name(name = "태그목록")
    private Set<String> tagTitleList;

    @Name(name = "평점")
    private float rating;

    @Name(name = "가격")
    private int price;

    @Name(name = "강의 공개 일자")
    private LocalDateTime openingDate;
}

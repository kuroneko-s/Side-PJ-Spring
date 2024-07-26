package com.choidh.service.learning.vo;

import com.choidh.service.common.annotation.Name;
import com.choidh.service.joinTables.entity.LearningTagJoinTable;
import com.choidh.service.learning.entity.Learning;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.springframework.hateoas.RepresentationModel;

import java.util.HashSet;
import java.util.Objects;
import java.util.Set;

@Getter @Setter
@NoArgsConstructor @AllArgsConstructor
public class LearningVO extends RepresentationModel<LearningVO> {
    @Override
    public String toString() {
        return "LearningVO{" +
                "id=" + id +
                ", title='" + title + '\'' +
                ", subscription='" + subscription + '\'' +
                ", simpleSubscription='" + simpleSubscription + '\'' +
                ", price=" + price +
                ", mainCategory='" + mainCategory + '\'' +
                ", subCategory='" + subCategory + '\'' +
                ", openingDate='" + openingDate + '\'' +
                ", opening=" + opening +
                ", rating=" + rating +
                ", professionalAccountName='" + professionalAccountName + '\'' +
                ", professionalAccountDescription='" + professionalAccountDescription + '\'' +
                ", professionalAccountHistory='" + professionalAccountHistory + '\'' +
                '}';
    }

    @Name(name = "강의 고유값")
    private Long id;

    @Name(name = "강의 제목")
    private String title;

    @Name(name = "강의 소개")
    private String subscription;

    @Name(name = "간단 간단 소개")
    private String simpleSubscription;

    @Name(name = "가격")
    private int price;

    @Name(name = "대분류 카테고리")
    private String mainCategory;

    @Name(name = "소분류 카테고리")
    private String subCategory;

    @Name(name = "강의 공개 시작 일시")
    private String openingDate;

    @Name(name = "강의 오픈 여부")
    private boolean opening = false;

    @Name(name = "강의 평점")
    private float rating = 0;

    @Name(name = "강사 이름")
    private String professionalAccountName;

    @Name(name = "강사 설명")
    private String professionalAccountDescription;

    @Name(name = "강사 경력")
    private String professionalAccountHistory;

    @Name(name = "강의 관련 태그들")
    private Set<LearningTagJoinTable> tags = new HashSet<>();

    public LearningVO(Learning learning) {
        this.id = Objects.requireNonNullElse(learning.getId(), -1L);
        this.title = Objects.requireNonNullElse(learning.getTitle(), "");
        this.subscription = Objects.requireNonNullElse(learning.getSubscription(), "");
        this.simpleSubscription = Objects.requireNonNullElse(learning.getSimpleSubscription(), "");
        this.price = learning.getPrice();
        this.mainCategory = Objects.requireNonNullElse(learning.getMainCategory(), "");
        this.subCategory = Objects.requireNonNullElse(learning.getSubCategory(), "");
        this.openingDate = learning.getOpeningDate().toString();
        this.opening = learning.isOpening();
        this.rating = learning.getRating();
        if (learning.getProfessionalAccount() != null) {
            this.professionalAccountName = learning.getProfessionalAccount().getName();
            this.professionalAccountDescription = learning.getProfessionalAccount().getDescription();
            this.professionalAccountHistory = learning.getProfessionalAccount().getHistory();
        }
        this.tags = new HashSet<>(learning.getTags());
    }
}

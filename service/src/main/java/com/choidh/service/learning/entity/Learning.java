package com.choidh.service.learning.entity;


import com.choidh.service.account.entity.ProfessionalAccount;
import com.choidh.service.annotation.Name;
import com.choidh.service.attachment.entity.AttachmentGroup;
import com.choidh.service.common.entity.BaseEntity;
import com.choidh.service.joinTables.entity.LearningCartJoinTable;
import com.choidh.service.joinTables.entity.LearningTagJoinTable;
import com.choidh.service.notice.entity.Notice;
import com.choidh.service.purchaseHistory.entity.PurchaseHistory;
import com.choidh.service.question.entity.Question;
import com.choidh.service.review.entity.Review;
import lombok.*;
import org.hibernate.annotations.BatchSize;

import javax.persistence.*;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

@Entity
@Getter
@Setter
@Builder
@ToString
@EqualsAndHashCode(of = "id", callSuper = false)
@AllArgsConstructor
@NoArgsConstructor
@BatchSize(size = 50)
public class Learning extends BaseEntity {
    @Id
    @GeneratedValue
    private Long id;

    @Name(name = "강의 제목")
    private String title;

    @Name(name = "간단 간단 소개")
    private String simpleSubscription;

    @Lob
    @Name(name = "강의 소개")
    private String subscription;

    @Name(name = "대분류 카테고리")
    private String mainCategory;

    @Name(name = "소분류 카테고리")
    private String subCategory;

    @Name(name = "가격")
    private int price;

    @Name(name = "강의 공개 시작 일시")
    private LocalDateTime openingDate = null;

    @Name(name = "강의 공개 종료 일시")
    private LocalDateTime closingDate = null;

    @Name(name = "강의 오픈 여부")
    private boolean opening = false;

    @Name(name = "강의 평점")
    private float rating = 0;

    @OneToOne
    @Name(name = "강의에 해당하는 파일들.", description = "AttachmentFile 의 Type 으로 구분.")
    private AttachmentGroup attachmentGroup;

    @OneToMany(mappedBy = "learning")
    @Name(name = "강의 관련 태그들")
    private Set<LearningTagJoinTable> tags = new HashSet<>();

    @OneToMany(mappedBy = "learning")
    @Name(name = "강의 관련 질문글들")
    private Set<Question> questions = new HashSet<>();

    @OneToMany(mappedBy = "learning")
    @Name(name = "강의 관련 리뷰들")
    private Set<Review> reviews = new HashSet<>();

    @OneToMany(mappedBy = "learning")
    @Name(name = "구매이력")
    private Set<PurchaseHistory> purchaseHistories = new HashSet<>();

    @ManyToOne
    @JoinColumn(name = "professional_account_id")
    @Name(name = "강의자")
    private ProfessionalAccount professionalAccount;

    @OneToMany(mappedBy = "learning")
    @Name(name = "카트에 추가되어 있는 수.", description = "유저가 카트에 추가한 강의들을 나타냄.")
    private Set<LearningCartJoinTable> carts = new HashSet<>();

    @OneToMany(mappedBy = "learning")
    @Name(name = "공지사항")
    private Set<Notice> noticesList = new HashSet<>();

    public void setTags(LearningTagJoinTable learningCartJoinTable) {
        this.tags.add(learningCartJoinTable);

        if (learningCartJoinTable.getLearning() == null) {
            learningCartJoinTable.setLearning(this);
        }
    }

    /**
     * 강의 오픈 처리
     */
    public void setOpening(boolean opening) {
        this.opening = opening;

        if (opening) this.openingDate = LocalDateTime.now();
        else this.closingDate = LocalDateTime.now();
    }

    /**
     * 강의자 추가
     */
    public void setProfessionalAccount(ProfessionalAccount professionalAccount) {
        this.professionalAccount = professionalAccount;

        if (!professionalAccount.getLearningList().contains(this)) {
            professionalAccount.getLearningList().add(this);
        }
    }

    public void setReviews(Review review) {
        this.getReviews().add(review);

        if (review.getLearning() != this) review.setLearning(this);

        double sum = this.getReviews().stream().mapToDouble(Review::getRating).sum();
        int ratingLength = this.getReviews().size();
        this.setRating((float) (sum / ratingLength));
    }

    public int getRatingInt(){
        return (int)Math.floor(this.rating);
    }

    public boolean checkRatingBoolean(){
        int rating_double = getRatingInt();
        return ((this.rating * 10) - (rating_double * 10)) >= 5 && rating_double <= 5;
    }

    public int emptyRating(){
        int rating_double = getRatingInt();
        boolean halfRating = checkRatingBoolean();
        return 5 - rating_double - (halfRating ? 1 : 0);
    }

    public String priceComma(){
        StringBuilder str = new StringBuilder().append(this.price);
        int index = str.length();
        int insertTime = index - 3;
        float i = (float) index / 3;

        if(index <= 1){
            return "free";
        }

        for (int j=0; j < i; j++){
            if(index % 3 <= 0){
                i--;
            }
            if(index <= 3){
                return String.valueOf(str);
            }
            str.insert(insertTime, ',');
            insertTime -= 3;
        }
        return String.valueOf(str);
    }

    public String getCategoryValue(){
        switch (this.mainCategory) {
            case "1":
                return "Web";
            case "2":
                return "algorithm";
            default:
                return "none";
        }
    }
}

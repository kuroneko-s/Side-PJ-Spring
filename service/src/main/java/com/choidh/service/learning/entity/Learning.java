package com.choidh.service.learning.entity;


import com.choidh.service.account.entity.Account;
import com.choidh.service.annotation.Name;
import com.choidh.service.question.entity.Question;
import com.choidh.service.review.entity.Review;
import com.choidh.service.tag.entity.Tag;
import com.choidh.service.video.entity.Video;
import lombok.*;

import javax.persistence.*;
import java.time.LocalDateTime;
import java.util.HashSet;
import java.util.Set;

@Entity
@Getter
@Setter
@Builder
@EqualsAndHashCode(of = "id")
@AllArgsConstructor
@NoArgsConstructor
public class Learning {
    @Id
    @GeneratedValue
    private Long id;

    @Name(name = "강의 제목")
    private String title;

    @Name(name = "강사 이름")
    private String lecturerName;

    @Name(name = "강사 설명")
    private String lecturerDescription;

    @Name(name = "간단 소개 (강의?)")
    private String simpleSubscription;

    @Lob
    @Name(name = "강의 소개")
    private String subscription;

    @Lob
    @Name(name = "배너 이미지 데이터 (base64)", description = "나중에 서버에 저장하고 경로 저장해서 들고오는거로 쓸 듯 이렇게 쓰면 안됨")
    private String bannerBytes;

    @Name(name = "배너 이미지 경로")
    private String bannerServerPath;

    @Name(name = "카테고리", description = "왜 구분값이나 그런게 없고 list 같은 형식이 아닐까 ?")
    private String kategorie;

    @Name(name = "가격")
    private int price;

    @Name(name = "강의 생성 일시")
    private LocalDateTime createLearning;

    @Name(name = "강의 시작 일시")
    private LocalDateTime openLearning = null;

    @Name(name = "강의 종료 일시")
    private LocalDateTime closeLearning = null;

    @Name(name = "강의 영상 갱신 일시")
    private LocalDateTime uploadVideo = null;

    @Name(name = "강의 갱신 일시")
    private LocalDateTime updateLearning = null;

    @Name(name = "구매 일시", description = "???????? 판매된 일시가 아닐까 ?")
    private LocalDateTime buyLearning;

    @Name(name = "강의 오픈 여부")
    private boolean startingLearning = false;

    @Name(name = "강의 폐쇄 여부")
    private boolean closedLearning = true;

    @Name(name = "강의 갯수")
    private int videoCount = 0;

    @Name(name = "강의 후기")
    private String comment;

    @Name(name = "강의 평점")
    private float rating = 0;

    @Name(name = "총 가격 ?")
    private Integer totalPrice;

    @ManyToMany(fetch = FetchType.LAZY)
    @Name(name = "수강중인 사람들 목록")
    private Set<Account> accounts = new HashSet<>();

    @ManyToOne
    @JoinColumn(name = "account_id")
    @Name(name = "업로더 (게시자)")
    private Account account;

    @OneToMany(mappedBy = "learning", fetch = FetchType.LAZY)
    @Name(name = "비디오 목록들")
    private Set<Video> videos = new HashSet<>();

    @ManyToMany
    @Name(name = "강의 관련 태그들")
    private Set<Tag> tags = new HashSet<>();

    @OneToMany(mappedBy = "learning")
    @Name(name = "강의 관련 질문글들")
    private Set<Question> questions = new HashSet<>();

    @OneToMany(mappedBy = "learning")
    @Name(name = "강의 관련 리뷰들")
    private Set<Review> reviews = new HashSet<>();

    public void setVideos(Video video) {
        this.getVideos().add(video);

        if (video.getLearning() != this) video.setLearning(this);
    }

    public void setReviews(Review review) {
        this.getReviews().add(review);

        if (review.getLearning() != this) review.setLearning(this);

        double sum = this.getReviews().stream().mapToDouble(Review::getRating).sum();
        int ratingLength = this.getReviews().size();
        this.setRating((float) (sum / ratingLength));
    }

    public void setAccount(Account account) {
        this.account = account;
        if(!account.getLearnings().contains(this)){
            account.getLearnings().add(this);
        }
    }
    public int getRating_int(){
        return (int)Math.floor(this.rating);
    }
    public boolean checkRating_boolean(){
        int rating_double = getRating_int();
        return ((this.rating * 10) - (rating_double * 10)) >= 5 && rating_double <= 5;
    }
    public int emptyRating(){
        int rating_double = getRating_int();
        boolean halfRating = checkRating_boolean();
        return 5 - rating_double - (halfRating ? 1 : 0);
    }
    public String price_comma(){
        StringBuilder str = new StringBuilder().append(this.price);
        int index = str.length();
        int insertTime = index - 3;
        float i = index / 3;

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
    public boolean checkUploadLearning(){
        return !(this.uploadVideo instanceof LocalDateTime) || this.uploadVideo == null;
    }
    public boolean checkUpdateLearning(){
        return !(this.updateLearning instanceof LocalDateTime) || this.updateLearning == null;
    }
    public String getKategorieValue(){
        switch (this.kategorie) {
            case "1":
                return "Web";
            case "2":
                return "algorithm";
            default:
                return "none";
        }
    }

    /**
     * 강의 구매 처리
     */
    public void buyLearning(Account account) {
        this.getAccounts().add(account); // 해당 강의를 수강하고 있는 유저 목록에 추가.

        // TODO: Learning 과 Account 간 조인 테이블 생성 필요할 듯. 검토 필요.
        // Learning 을 구매한 이력을 시간으로도 관리하고 싶으면 중간에 조인테이블을 만들어서 따로 관리를 해줘야함.
        // 근데 구매한 이력만을 가지고 조인테이블을 만들어서 관리할 이유가 있냐고 생각하면 있긴한데 ...
        this.setBuyLearning(LocalDateTime.now());
    }

    /**
     * 강의 구매 취소 처리
     */
    public void cancelLearning(Learning learning) {
        learning.getAccounts().remove(account); // 강의에서 수강중인 학생에서 삭제

        this.setBuyLearning(null);
    }
}

package com.choidh.service.account.entity;


import com.choidh.service.annotation.Name;
import com.choidh.service.common.entity.BaseDateEntity;
import com.choidh.service.learning.entity.Learning;
import com.choidh.service.notification.entity.Notification;
import com.choidh.service.question.entity.Question;
import com.choidh.service.review.entity.Review;
import com.choidh.service.tag.entity.Tag;
import lombok.*;

import javax.persistence.*;
import java.time.LocalDateTime;
import java.util.HashSet;
import java.util.Set;
import java.util.UUID;

/**
 * 유저 Entity
 */

@Entity
@Getter
@Setter
@Builder
@EqualsAndHashCode(of = "id", callSuper = false)
@AllArgsConstructor
@NoArgsConstructor
public class Account extends BaseDateEntity {
    @Id
    @GeneratedValue
    private Long id;

    private String password;

    @Column(unique = true)
    private String nickname;

    @Column(unique = true)
    private String email;

    @Lob
    private String description;

    // private LocalDateTime createAccount;
    private LocalDateTime createEmailToken;

    private String emailCheckToken;
    private boolean tokenChecked = false;

    //notifications
    private boolean siteMailNotification;
    private boolean siteWebNotification;
    private boolean learningMailNotification;
    private boolean learningWebNotification;

    //uploader
    private boolean uploader = true;

    //cart List
    @ManyToMany
    private Set<Learning> cartList;

    @ManyToMany(fetch = FetchType.EAGER)
    private Set<Tag> tags = new HashSet<>();

    //can listen Learning buy or free videos
    @ManyToMany(mappedBy = "accounts")
    @Name(name = "현재 수강중인 강의 목록")
    private Set<Learning> listenLearning = new HashSet<>();

    @OneToMany(mappedBy = "account")
    private Set<Learning> learnings = new HashSet<>();

    @OneToMany(mappedBy = "account")
    private Set<Question> questions = new HashSet<>();

    @OneToMany(mappedBy = "account")
    private Set<Review> reviews = new HashSet<>();

    @OneToMany(mappedBy = "account")
    private Set<Notification> notifications = new HashSet<>();

    public void setLearnings(Learning learning) {
        this.getLearnings().add(learning);

        if (learning.getAccount() != this) learning.setAccount(this);
    }

    public void setReviews(Review review) {
        this.getReviews().add(review);

        if (review.getAccount() != this) review.setAccount(this);
    }

    // 랜덤 이메일 토큰 생성.
    public void createEmailCheckToken(){
        this.emailCheckToken = UUID.randomUUID().toString();
        this.createEmailToken = LocalDateTime.now();
    }

    // 이메일 전송 가능여부 확인 (1시간)
    public Boolean canCheckingEmailToken() {
        return this.createEmailToken.isBefore(LocalDateTime.now().minusHours(1));
    }

    public boolean canUploadVideo(){
        return !this.learnings.isEmpty();
    }

    /**
     * 강의 구매 처리
     */
    public void buyLearning(Learning learning) {
        this.getListenLearning().add(learning); // 수강중인 강의 목록 추가
        this.getCartList().remove(learning); // 카트에서 구매한 강의 삭제
    }

    /**
     * 강의 구매 취소 처리
     */
    public void cancelLearning(Learning learning) {
        this.getListenLearning().remove(learning); // 현재 수강중인 강의에서 삭제

        // 강의를 다시 카트에 넣고 싶으면 Alert 로 띄워줘도 되고 취소한 화면에서 다시 카트에 넣어줄 수 있도록 개선해도 됨.
    }
}

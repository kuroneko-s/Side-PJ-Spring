package com.choidh.service.account.entity;


import com.choidh.service.account.vo.AccountType;
import com.choidh.service.annotation.Name;
import com.choidh.service.cart.entity.Cart;
import com.choidh.service.common.entity.BaseEntity;
import com.choidh.service.joinTables.entity.AccountTagJoinTable;
import com.choidh.service.purchaseHistory.entity.PurchaseHistory;
import com.choidh.service.question.entity.Question;
import com.choidh.service.review.entity.Review;
import lombok.*;
import org.hibernate.annotations.BatchSize;

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
@BatchSize(size = 50)
public class Account extends BaseEntity {
    @Id
    @GeneratedValue
    @Column(name = "account_id")
    private Long id;

    @Column(unique = true)
    @Name(name = "별명")
    private String nickname;

    @Column(unique = true)
    @Name(name = "이메일", description = "로그인 시 아이디로 사용")
    private String email;

    @Name(name = "패스워드", description = "sha256 암호화")
    private String password;

    @Column(length = 500)
    @Name(name = "자기소개")
    private String description;

    @Name(name = "이메일 인증용 랜덤 토큰")
    private String tokenForEmailForAuthentication;

    @Name(name = "이메일 인증 시 생성된 시간 기록.")
    private LocalDateTime createTimeOfEmailToken;

    @Name(name = "이메일 인증 여부")
    private boolean checked = false;

    @Name(name = "사이트 관련 메일 알람 설정")
    private boolean siteMailNotification;

    @Name(name = "사이트 관련 웹 알람 설정")
    private boolean siteWebNotification;

    @Name(name = "강의 관련 메일 알람 설정")
    private boolean learningMailNotification;

    @Name(name = "강의 관련 웹 알람 설정")
    private boolean learningWebNotification;

    @OneToOne(mappedBy = "account")
    @Name(name = "업로더용 계정")
    private ProfessionalAccount professionalAccount = null;

    @OneToOne(mappedBy = "account")
    @Name(name = "장바구니 목록")
    private Cart cart;

    @OneToMany(mappedBy = "account")
    @Name(name = "본인 선호 기술 스택 목록")
    private Set<AccountTagJoinTable> tags = new HashSet<>();

    @OneToMany(mappedBy = "account")
    @Name(name = "질의 글 목록")
    private Set<Question> questions = new HashSet<>();

    @OneToMany(mappedBy = "account")
    @Name(name = "리뷰 글 목록")
    private Set<Review> reviews = new HashSet<>();

    @OneToMany(mappedBy = "account")
    @Name(name = "구매 이력 목록")
    private Set<PurchaseHistory> purchaseHistories = new HashSet<>();

    @Enumerated(EnumType.STRING)
    private AccountType accountType;

    public void setReviews(Review review) {
        this.getReviews().add(review);

        if (review.getAccount() != this) review.setAccount(this);
    }

    // 랜덤 이메일 토큰 생성.
    public void createTokenForEmailForAuthentication(){
        this.tokenForEmailForAuthentication = UUID.randomUUID().toString();
        this.createTimeOfEmailToken = LocalDateTime.now();
    }

    // 이메일 전송 가능여부 확인 (1시간)
    public Boolean canCheckingEmailToken() {
        return this.createTimeOfEmailToken.isBefore(LocalDateTime.now().minusHours(1));
    }
}

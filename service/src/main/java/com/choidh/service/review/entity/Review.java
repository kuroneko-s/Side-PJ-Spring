package com.choidh.service.review.entity;


import com.choidh.service.account.entity.Account;
import com.choidh.service.common.entity.BaseDateEntity;
import com.choidh.service.learning.entity.Learning;
import lombok.*;

import javax.persistence.*;
import java.time.LocalDateTime;

@Entity
@EqualsAndHashCode(of = "id", callSuper = false)
@Builder
@ToString
@Getter
@Setter
@NoArgsConstructor @AllArgsConstructor
public class Review extends BaseDateEntity {
    @Id @Column(name = "review_id")
    @GeneratedValue
    private Long id;

    private float rating;
    private String description;
    private LocalDateTime createTime;

    @ManyToOne
    @JoinColumn(name = "account_id")
    private Account account;

    @ManyToOne
    @JoinColumn(name = "learning_id")
    private Learning learning;

    /**
     * 리뷰를 각각의 Account, Learning 에 추가.
     */
    public void addReview(Account account, Learning learning) {
        account.setReviews(this);
        learning.setReviews(this);
    }
}

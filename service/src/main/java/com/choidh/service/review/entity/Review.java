package com.choidh.service.review.entity;


import com.choidh.service.account.entity.Account;
import com.choidh.service.common.entity.BaseDateEntity;
import com.choidh.service.learning.entity.Learning;
import lombok.*;
import org.hibernate.annotations.BatchSize;

import javax.persistence.*;
import java.time.LocalDateTime;

@Entity
@EqualsAndHashCode(of = "id", callSuper = false)
@Builder
@ToString
@Getter
@Setter
@NoArgsConstructor @AllArgsConstructor
@BatchSize(size = 50)
public class Review extends BaseDateEntity {
    @Id @Column(name = "review_id")
    @GeneratedValue
    private Long id;

    private float rating;
    private String description;
    private LocalDateTime createTime;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "account_id")
    private Account account;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "learning_id")
    private Learning learning;
}

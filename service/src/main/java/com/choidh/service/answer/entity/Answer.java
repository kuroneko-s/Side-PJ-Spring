package com.choidh.service.answer.entity;

import com.choidh.service.account.entity.Account;
import com.choidh.service.common.entity.BaseEntity;
import com.choidh.service.question.entity.Question;
import lombok.*;
import org.hibernate.annotations.BatchSize;

import javax.persistence.*;

@Entity
@Getter
@Builder
@EqualsAndHashCode(of = "id", callSuper = false)
@AllArgsConstructor
@NoArgsConstructor
@BatchSize(size = 50)
public class Answer extends BaseEntity {
    @Id
    @GeneratedValue
    @Column(name = "answer_id")
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    private Account account;

    @ManyToOne(fetch = FetchType.LAZY)
    private Question question;
}

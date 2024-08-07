package com.choidh.service.question.entity;


import com.choidh.service.account.entity.Account;
import com.choidh.service.common.annotation.Name;
import com.choidh.service.common.entity.BaseEntity;
import com.choidh.service.learning.entity.Learning;
import com.fasterxml.jackson.annotation.JsonBackReference;
import lombok.*;
import org.hibernate.annotations.BatchSize;

import javax.persistence.*;
import java.time.LocalDateTime;

@Entity
@EqualsAndHashCode(of = "id", callSuper = false)
@Builder
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@BatchSize(size = 50)
public class Question extends BaseEntity {
    @Id @Column(name = "question_id")
    @GeneratedValue
    private Long id;

    @Name(name = "질문 제목")
    private String title;

    @Column(length = 1500)
    @Name(name = "질문 내용")
    private String description;

    @Column(length = 1500)
    @Name(name = "답변 내용")
    private String answer;

    @Name(name = "질문 등록 시간")
    private LocalDateTime questionTime;

    @Name(name = "답변 등록 시간")
    private LocalDateTime answerTime;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "learning_id")
    @JsonBackReference
    private Learning learning;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "account_id")
    @JsonBackReference
    private Account account;

    public void setAccount(Account account) {
        this.account = account;

        account.getQuestions().add(this);
    }

    public void setLearning(Learning learning) {
        this.learning = learning;

        learning.getQuestions().add(this);
    }
}

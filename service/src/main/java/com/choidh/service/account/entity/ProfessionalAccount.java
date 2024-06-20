package com.choidh.service.account.entity;

import com.choidh.service.annotation.Name;
import com.choidh.service.common.entity.BaseEntity;
import com.choidh.service.learning.entity.Learning;
import com.choidh.service.question.entity.Question;
import lombok.*;
import org.hibernate.annotations.BatchSize;

import javax.persistence.*;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

/**
 * 강의자 관련 계정.
 */

@Entity
@Getter
@Setter
@Builder
@EqualsAndHashCode(of = "id", callSuper = false)
@AllArgsConstructor
@NoArgsConstructor
@BatchSize(size = 50)
public class ProfessionalAccount extends BaseEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE)
    @Column(name = "professional_account_id")
    private Long id;

    @OneToOne
    private Account account;

    @Name(name = "강사 이름")
    private String name;

    @Name(name = "강사 설명")
    private String description;

    @Name(name = "강사 경력")
    private String history;

    @OneToMany
    private Set<Learning> learningList = new HashSet<>();

    public void setLearningList(Learning learning) {
        this.learningList.add(learning);

        if (learning.getProfessionalAccount() == null) {
            learning.setProfessionalAccount(this);
        }
    }
}

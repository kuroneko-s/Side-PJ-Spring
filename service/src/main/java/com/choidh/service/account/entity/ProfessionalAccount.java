package com.choidh.service.account.entity;

import com.choidh.service.annotation.Name;
import com.choidh.service.common.entity.BaseEntity;
import com.choidh.service.learning.entity.Learning;
import lombok.*;
import org.hibernate.annotations.BatchSize;

import javax.persistence.*;
import java.util.ArrayList;
import java.util.List;

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
    @GeneratedValue
    @Column(name = "professional_account_id")
    private Long id;

    @OneToOne(fetch = FetchType.LAZY)
    private Account account;

    @Name(name = "강사 이름")
    private String name;

    @Name(name = "강사 설명")
    private String description;

    @Name(name = "강사 경력")
    private String history;

    @OneToMany
    private List<Learning> learningList = new ArrayList<>();
}

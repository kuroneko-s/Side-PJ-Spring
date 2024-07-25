package com.choidh.service.professional.entity;

import com.choidh.service.account.entity.Account;
import com.choidh.service.common.annotation.Name;
import com.choidh.service.common.entity.BaseEntity;
import com.choidh.service.learning.entity.Learning;
import com.fasterxml.jackson.annotation.JsonBackReference;
import com.fasterxml.jackson.annotation.JsonManagedReference;
import lombok.*;
import org.hibernate.annotations.BatchSize;

import javax.persistence.*;
import java.util.HashSet;
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
    @JsonBackReference
    private Account account;

    @Name(name = "강사 이름")
    private String name;

    @Name(name = "강사 설명")
    private String description;

    @Name(name = "강사 경력")
    private String history;

    @Name(name = "허가 유무")
    private boolean used;

    @OneToMany(mappedBy = "professionalAccount")
    @JsonManagedReference
    private Set<Learning> learningSet = new HashSet<>();

    public void setLearningSet(Learning learning) {
        this.learningSet.add(learning);

        if (learning.getProfessionalAccount() == null) {
            learning.setProfessionalAccount(this);
        }
    }
}

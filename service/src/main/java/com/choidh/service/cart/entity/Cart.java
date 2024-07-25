package com.choidh.service.cart.entity;

import com.choidh.service.account.entity.Account;
import com.choidh.service.common.entity.BaseEntity;
import com.choidh.service.joinTables.entity.LearningCartJoinTable;
import com.fasterxml.jackson.annotation.JsonBackReference;
import com.fasterxml.jackson.annotation.JsonManagedReference;
import lombok.*;
import org.hibernate.annotations.BatchSize;

import javax.persistence.*;
import java.util.HashSet;
import java.util.Set;

@Entity
@Getter
@Builder
@EqualsAndHashCode(of = "id", callSuper = false)
@AllArgsConstructor
@NoArgsConstructor
@BatchSize(size = 50)
public class Cart extends BaseEntity {
    @Id
    @GeneratedValue
    @Column(name = "cart_id")
    private Long id;

    @OneToOne
    @JsonBackReference
    private Account account;

    @OneToMany(mappedBy = "cart")
    @JsonManagedReference
    private Set<LearningCartJoinTable> learningCartJoinTables = new HashSet<>();

    public void setAccount(Account account) {
        this.account = account;
        account.setCart(this);
    }

    public void addLearningCartJoinTable(LearningCartJoinTable learningCartJoinTable) {
        this.learningCartJoinTables.add(learningCartJoinTable);

        if (learningCartJoinTable.getCart() == null) {
            learningCartJoinTable.setCart(this);
        }
    }

    public void setLearningCartJoinTables(Set<LearningCartJoinTable> learningCartJoinTables) {
        this.learningCartJoinTables = new HashSet<>(learningCartJoinTables);
    }
}

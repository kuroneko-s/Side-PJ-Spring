package com.choidh.service.cart.entity;

import com.choidh.service.account.entity.Account;
import com.choidh.service.common.entity.BaseEntity;
import com.choidh.service.joinTables.entity.LearningCartJoinTable;
import lombok.*;
import org.hibernate.annotations.BatchSize;

import javax.persistence.*;
import java.util.ArrayList;
import java.util.List;

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

    @OneToOne(fetch = FetchType.LAZY)
    @Setter
    private Account account;

    @OneToMany(mappedBy = "cart")
    private List<LearningCartJoinTable> learningCartJoinTables = new ArrayList<>();

    public void addLearningCartJoinTable(LearningCartJoinTable learningCartJoinTable) {
        this.learningCartJoinTables.add(learningCartJoinTable);

        if (learningCartJoinTable.getCart() == null) {
            learningCartJoinTable.setCart(this);
        }
    }
}

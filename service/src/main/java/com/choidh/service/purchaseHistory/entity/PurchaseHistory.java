package com.choidh.service.purchaseHistory.entity;

import com.choidh.service.account.entity.Account;
import com.choidh.service.common.entity.BaseEntity;
import com.choidh.service.learning.entity.Learning;
import com.choidh.service.purchaseHistory.vo.PurchaseStatus;
import lombok.*;
import org.hibernate.annotations.BatchSize;

import javax.persistence.*;

/**
 * 구매이력
 */

@Entity
@Getter
@Setter
@Builder
@EqualsAndHashCode(of = "id", callSuper = false)
@AllArgsConstructor
@NoArgsConstructor
@BatchSize(size = 50)
public class PurchaseHistory extends BaseEntity {
    @Id
    @GeneratedValue
    @Column(name = "purchase_history_id")
    private Long id;

    @ManyToOne
    @JoinColumn(name = "account_id")
    private Account account;

    @ManyToOne
    @JoinColumn(name = "learning_id")
    private Learning learning;

    @Enumerated(EnumType.STRING)
    private PurchaseStatus purchaseStatus;
}

package com.choidh.service.joinTables.entity;

import com.choidh.service.account.entity.Account;
import com.choidh.service.common.entity.BaseDateEntity;
import com.choidh.service.tag.entity.Tag;
import lombok.*;
import org.hibernate.annotations.BatchSize;

import javax.persistence.*;

@Entity
@EqualsAndHashCode(of = "id", callSuper = false)
@Builder
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@BatchSize(size = 50)
public class AccountTagJoinTable extends BaseDateEntity {
    @Id
    @GeneratedValue
    @Column(name = "account_tag_id")
    private Long id;

    @JoinColumn(name = "account_id")
    @ManyToOne(fetch = FetchType.LAZY)
    private Account account;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "tag_id")
    private Tag tag;
}

package com.choidh.service.joinTables.entity;

import com.choidh.service.account.entity.Account;
import com.choidh.service.common.entity.BaseDateEntity;
import com.choidh.service.menu.entity.Menu;
import lombok.*;

import javax.persistence.*;

@Entity
@EqualsAndHashCode(of = "id", callSuper = false)
@Builder
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class AccountMenuJoinTable extends BaseDateEntity {
    @Id
    @GeneratedValue
    @Column(name = "account_menu_id")
    private Long id;

    @ManyToOne
    @JoinColumn(name = "account_id")
    private Account account;

    @ManyToOne
    @JoinColumn(name = "menu_id")
    private Menu menu;
}

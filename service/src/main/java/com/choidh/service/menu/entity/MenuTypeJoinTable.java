package com.choidh.service.menu.entity;

import com.choidh.service.account.vo.AccountType;
import com.choidh.service.annotation.Name;
import com.choidh.service.common.entity.BaseDateEntity;
import lombok.*;

import javax.persistence.*;

@Entity
@EqualsAndHashCode(of = "id", callSuper = false)
@Builder
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class MenuTypeJoinTable extends BaseDateEntity {
    @Id
    @GeneratedValue
    @Column(name = "menu_type_id")
    private Long id;

    @Enumerated(EnumType.STRING)
    private AccountType accountType;

    @Name(name = "활성화 유무")
    private boolean used = true;

    @ManyToOne
    private Menu menu;
}

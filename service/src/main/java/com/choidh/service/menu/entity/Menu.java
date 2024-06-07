package com.choidh.service.menu.entity;

import com.choidh.service.common.entity.BaseEntity;
import com.choidh.service.joinTables.entity.AccountMenuJoinTable;
import lombok.*;

import javax.persistence.*;
import java.util.HashSet;
import java.util.Set;

@Entity
@EqualsAndHashCode(of = "id", callSuper = false)
@Builder
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class Menu extends BaseEntity {
    @Id
    @GeneratedValue
    @Column(name = "menu_id")
    private Long id;

    @OneToMany(mappedBy = "menu")
    private Set<AccountMenuJoinTable> accounts = new HashSet<>();
}

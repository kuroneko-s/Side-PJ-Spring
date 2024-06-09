package com.choidh.service.menu.entity;

import com.choidh.service.annotation.Name;
import com.choidh.service.common.entity.BaseEntity;
import lombok.*;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;

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

    @Name(name = "이동 URL")
    private String url;

    @Name(name = "하위 메뉴인지 구분용")
    private Integer level;
}

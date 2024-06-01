package com.choidh.service.notice.entity;

import com.choidh.service.common.entity.BaseEntity;
import com.choidh.service.learning.entity.Learning;
import lombok.*;

import javax.persistence.*;

/**
 * 공지사항 Entity
 */

@Entity
@Getter @Setter @EqualsAndHashCode(of = "id", callSuper = false)
@NoArgsConstructor @AllArgsConstructor
@Builder
public class Notice extends BaseEntity {
    @Id
    @GeneratedValue
    private Long id;

    private String title;

    private String content;

    @ManyToOne
    @JoinColumn(name = "learning_id")
    private Learning learning;
}

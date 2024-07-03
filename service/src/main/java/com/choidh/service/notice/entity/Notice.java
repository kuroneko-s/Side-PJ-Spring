package com.choidh.service.notice.entity;

import com.choidh.service.common.entity.BaseEntity;
import com.choidh.service.learning.entity.Learning;
import com.choidh.service.notice.vo.NoticeType;
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

    @Enumerated(EnumType.STRING)
    private NoticeType noticeType;
}

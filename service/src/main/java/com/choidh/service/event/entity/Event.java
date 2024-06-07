package com.choidh.service.event.entity;

import com.choidh.service.annotation.Name;
import com.choidh.service.attachment.entity.AttachmentGroup;
import com.choidh.service.common.entity.BaseEntity;
import lombok.*;

import javax.persistence.*;

@Entity
@EqualsAndHashCode(of = "id", callSuper = false)
@Builder
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class Event extends BaseEntity {
    @Id
    @GeneratedValue
    @Column(name = "event_id")
    private Long id;

    @OneToOne
    @Name(name = "강의에 해당하는 파일들.", description = "AttachmentFile 의 Type 으로 구분.")
    private AttachmentGroup attachmentGroup;
}

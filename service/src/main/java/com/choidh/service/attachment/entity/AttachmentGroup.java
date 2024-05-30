package com.choidh.service.attachment.entity;

import com.choidh.service.common.entity.BaseEntity;
import lombok.*;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;

@Entity
@Getter
@Setter
@EqualsAndHashCode(of = "id", callSuper = false)
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class AttachmentGroup extends BaseEntity {
    @Id
    @GeneratedValue
    @Column(name = "attachment_group_id")
    private Long id;
}

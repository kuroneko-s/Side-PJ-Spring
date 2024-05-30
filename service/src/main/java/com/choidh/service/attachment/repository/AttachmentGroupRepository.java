package com.choidh.service.attachment.repository;

import com.choidh.service.attachment.entity.AttachmentGroup;
import org.springframework.data.jpa.repository.JpaRepository;

public interface AttachmentGroupRepository extends JpaRepository<AttachmentGroup, Long> {
}

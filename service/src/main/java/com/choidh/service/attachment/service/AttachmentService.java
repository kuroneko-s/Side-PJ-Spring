package com.choidh.service.attachment.service;

import com.choidh.service.attachment.entity.AttachmentFile;
import com.choidh.service.attachment.entity.AttachmentFileType;
import com.choidh.service.attachment.entity.AttachmentGroup;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;

public interface AttachmentService {

    AttachmentGroup createAttachmentGroup();

    void saveFile(AttachmentGroup attachmentGroup, MultipartFile multipartFile, AttachmentFileType attachmentFileType);

    List<AttachmentFile> getAttachmentFiles(Long attachmentGroupId, AttachmentFileType attachmentFileType);

    int cntAttachmentFiles(Long attachmentGroupId, AttachmentFileType attachmentFileType);

    AttachmentFile getAttachmentFileById(Long attachmentGroupId, Long attachmentFileId);

    int delAttachmentFile(Long attachmentGroupId, Long attachmentFileId);
}

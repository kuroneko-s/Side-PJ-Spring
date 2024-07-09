package com.choidh.service.attachment.service;

import com.choidh.service.attachment.entity.AttachmentFile;
import com.choidh.service.attachment.vo.AttachmentFileType;
import com.choidh.service.attachment.entity.AttachmentGroup;
import com.choidh.service.attachment.vo.ImageInfoVO;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;
import java.util.Map;

public interface AttachmentService {
    /**
     * AttachmentGroup 생성
     */
    AttachmentGroup createAttachmentGroup();

    /**
     * 파일 저장
     */
    void saveFile(AttachmentGroup attachmentGroup, MultipartFile multipartFile, AttachmentFileType attachmentFileType);

    /**
     * 파일 저장
     */
    void saveFile(AttachmentGroup attachmentGroup, MultipartFile multipartFile, AttachmentFileType attachmentFileType, String originalFileName);

    /**
     * 파일 목록조회 By 그룹 ID 및 타입
     */
    List<AttachmentFile> getAttachmentFiles(Long attachmentGroupId, AttachmentFileType attachmentFileType);

    /**
     * 파일 목록 카운트. By 그룹 ID 및 파일 타입
     */
    int cntAttachmentFiles(Long attachmentGroupId, AttachmentFileType attachmentFileType);

    /**
     * 파일 단건 조회 By 그룹 및 파일 ID
     */
    AttachmentFile getAttachmentFileById(Long attachmentFileId);

    /**
     * 파일 삭제(비활성화) By 그룹 및 파일 ID
     */
    int delAttachmentFile(Long attachmentFileId);

    /**
     * 배너 이미지 추출.
     */
    Map<Long, ImageInfoVO> getImageInfo(List<Long> attachmentGroupIdList, AttachmentFileType attachmentFileType);
}

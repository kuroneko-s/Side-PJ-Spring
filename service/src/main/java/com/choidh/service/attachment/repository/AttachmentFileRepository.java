package com.choidh.service.attachment.repository;

import com.choidh.service.attachment.entity.AttachmentFile;
import com.choidh.service.attachment.entity.AttachmentFileType;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;

import java.util.List;

public interface AttachmentFileRepository extends JpaRepository<AttachmentFile, Long> {
    /**
     * 파일 목록 조회 By 그룹 ID 및 파일 타입
     */
    @Query(value = "select file " +
            "from AttachmentFile file " +
            "where file.attachmentGroup.id = :attachmentGroupId " +
            "and file.attachmentFileType = :attachmentFileType " +
            "order by file.id")
    List<AttachmentFile> findAllByGroupAndType(Long attachmentGroupId, AttachmentFileType attachmentFileType);

    /**
     * 파일 단건 조회 By 그룹 및 파일 ID
     */
    @Query(value = "select file " +
            "from AttachmentFile file " +
            "where file.id = :attachmentFileId")
    AttachmentFile findByFileId(Long attachmentFileId);

    /**
     * 파일 삭제(비활성화) By 그룹 및 파일 ID
     */
    @Query(value = "update from AttachmentFile file " +
            "set file.isDelete = true " +
            "where file.id = :attachmentFileId")
    @Modifying
    int deleteByFileId(Long attachmentFileId);

    /**
     * 파일 목록 카운트. By 그룹 ID 및 파일 타입
     */
    @Query(value = "select count(*) " +
            "from AttachmentFile file " +
            "where file.attachmentGroup.id = :attachmentGroupId " +
            "and file.attachmentFileType = :attachmentFileType")
    int countByGroupAndType(Long attachmentGroupId, AttachmentFileType attachmentFileType);
}

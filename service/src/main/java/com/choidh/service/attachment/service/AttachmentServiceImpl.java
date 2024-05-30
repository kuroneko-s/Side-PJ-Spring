package com.choidh.service.attachment.service;

import com.choidh.service.attachment.entity.AttachmentFile;
import com.choidh.service.attachment.entity.AttachmentFileType;
import com.choidh.service.attachment.entity.AttachmentGroup;
import com.choidh.service.attachment.repository.AttachmentFileRepository;
import com.choidh.service.attachment.repository.AttachmentGroupRepository;
import com.choidh.service.common.FileUtils;
import com.choidh.service.common.StringUtils;
import com.choidh.service.common.exception.FileCantDeleteException;
import com.choidh.service.common.exception.FileNotSavedException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.io.IOUtils;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.io.Resource;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import java.io.*;
import java.nio.file.FileSystemNotFoundException;
import java.util.List;

import static com.choidh.service.common.AppConstant.isNotImageFile;

@Slf4j
@Service
@Transactional(readOnly = true)
@RequiredArgsConstructor
public class AttachmentServiceImpl implements AttachmentService {
    private final AttachmentGroupRepository attachmentGroupRepository;
    private final AttachmentFileRepository attachmentFileRepository;

    @Value("${download.path}")
    private String downloadPath;

    // 신규 그룹 생성
    @Override
    public AttachmentGroup createAttachmentGroup() {
        return attachmentGroupRepository.save(new AttachmentGroup());
    }

    // 파일 저장
    @Override
    @Transactional
    public void saveFile(AttachmentGroup attachmentGroup, MultipartFile multipartFile, AttachmentFileType attachmentFileType) {
        AttachmentFile beforeAttachmentFile = new AttachmentFile(attachmentGroup);
        AttachmentFile newAttachmentFile = attachmentFileRepository.save(beforeAttachmentFile);
        String groupSn = StringUtils.padLeftUsingFormat(attachmentGroup.getId().toString(), 7, '0');
        String fileSn = StringUtils.padLeftUsingFormat(newAttachmentFile.getId().toString(), 7, '0');

        String filePath = FileUtils.getFilePath();
        String fileName = groupSn + fileSn;
        String fileExtension = FileUtils.getFileExtension(multipartFile);

        if (isNotImageFile(fileExtension)) throw new IllegalArgumentException("이미지 파일이 아닙니다.");

        newAttachmentFile.setFileSize(multipartFile.getSize());
        newAttachmentFile.setOriginalFileName(multipartFile.getOriginalFilename());
        newAttachmentFile.setFileName(fileName);
        newAttachmentFile.setPath(filePath);
        newAttachmentFile.setExtension(fileExtension);
        newAttachmentFile.setAttachmentFileType(attachmentFileType);

        // 파일 저장
        Resource resource = multipartFile.getResource();
        try(
                BufferedInputStream inputStream = new BufferedInputStream(resource.getInputStream());
                BufferedOutputStream outputStream = new BufferedOutputStream(new FileOutputStream(newAttachmentFile.getFullPath(downloadPath)), 1024 * 500)
        ){
            // 위치 폴더 생성
            FileUtils.createDir(filePath);

            // 파일 저장
            IOUtils.copy(inputStream, outputStream);
            outputStream.flush();
        } catch (IOException e) {
            // 로컬에 파일 저장에 실패했을 경우 기존 DB 정보도 삭제.
            attachmentFileRepository.delete(newAttachmentFile);
            throw new FileNotSavedException(e);
        }
    }

    // 파일 목록조회 By 그룹 ID 및 타입
    @Override
    public List<AttachmentFile> getAttachmentFiles(Long attachmentGroupId, AttachmentFileType attachmentFileType) {
        return attachmentFileRepository.findAllByGroupAndType(attachmentGroupId, attachmentFileType);
    }

    // 파일 갯수 조회
    @Override
    public int cntAttachmentFiles(Long attachmentGroupId, AttachmentFileType attachmentFileType) {
        return attachmentFileRepository.countByGroupAndType(attachmentGroupId, attachmentFileType);
    }

    // 파일 단건조회 By 그룹 및 파일 ID
    @Override
    public AttachmentFile getAttachmentFileById(Long attachmentGroupId, Long attachmentFileId) {
        return attachmentFileRepository.findByGroupIdAndFileId(attachmentGroupId, attachmentFileId);
    }

    // 파일 삭제
    @Override
    public int delAttachmentFile(Long attachmentGroupId, Long attachmentFileId) {
        // 파일 정보 읽기
        AttachmentFile attachmentFile = attachmentFileRepository.findByGroupIdAndFileId(attachmentGroupId, attachmentFileId);

        // 서버에서 해당 파일 삭제
        File file = new File(attachmentFile.getFullPath(downloadPath));
        try {
            if (file.exists())
                if (!file.delete())
                    throw new FileCantDeleteException();
            else throw new FileSystemNotFoundException();
        } catch (Exception exception) {
            log.error(exception.getMessage(), exception);

            throw new FileCantDeleteException();
        }

        // 삭제 쿼리를 날릴거면 다른 테이블에서 백업을 하고 지워야하는데 지금 그정도는 아니니깐 비활성화 정도만 진행
        // attachmentFileRepository.deleteByGroupIdAndFileId(attachmentGroupId, attachmentFileId);

        // 파일 관련 DB 컬럼 비활성화.
        return attachmentFileRepository.deleteByGroupIdAndFileId(attachmentGroupId, attachmentFileId);
    }
}

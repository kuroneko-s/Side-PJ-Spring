package com.choidh.web.event;

import com.choidh.service.attachment.entity.AttachmentFile;
import com.choidh.service.attachment.entity.AttachmentFileType;
import com.choidh.service.attachment.entity.AttachmentGroup;
import com.choidh.service.attachment.repository.AttachmentFileRepository;
import com.choidh.service.attachment.service.AttachmentService;
import com.choidh.service.common.FileUtils;
import com.choidh.service.common.StringUtils;
import com.choidh.service.common.exception.FileNotSavedException;
import com.choidh.service.event.entity.Event;
import com.choidh.service.event.service.EventService;
import com.choidh.service.event.vo.RegEventVO;
import com.choidh.service.notification.service.NotificationService;
import com.choidh.service.notification.vo.NotificationType;
import com.choidh.service.notification.vo.RegNotificationVO;
import org.apache.commons.io.IOUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.ApplicationArguments;
import org.springframework.boot.ApplicationRunner;
import org.springframework.core.io.PathResource;
import org.springframework.core.io.Resource;
import org.springframework.core.io.support.SpringFactoriesLoader;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import java.io.*;
import java.nio.file.Path;
import java.util.List;

@Component
public class EventAppRunner implements ApplicationRunner {
    @Autowired private EventService eventService;
    @Autowired private AttachmentService attachmentService;
    @Autowired private AttachmentFileRepository attachmentFileRepository;
    @Autowired private NotificationService notificationService;

    @Value("${download.path}")
    private String downloadPath;

    @Override
    @Transactional
    public void run(ApplicationArguments args) throws Exception {
        List<Event> eventList = eventService.getEventList();

        if (eventList.isEmpty()) {
            AttachmentGroup attachmentGroup = attachmentService.createAttachmentGroup();

            // 임의의 이미지 파일들 추가.
            createFile(attachmentGroup);

            Event event = eventService.regEvent(RegEventVO.builder()
                    .attachmentGroup(attachmentGroup)
                    .used(true)
                    .build());

            // 이벤트 생성 후 알림 추가해줘야함...
            notificationService.regNotification(RegNotificationVO.builder()
                    .title("새로운 이벤트가 생겼어요.")
                    .description("내용은 테스트라는 겁니다.")
                    .event(event)
                    .notificationType(NotificationType.EVENT)
                    .build());
        }
    }

    private void createFile(AttachmentGroup attachmentGroup) throws IOException {
        Resource resource = new PathResource(Path.of("/Users/kuroneko/Documents/PJ-java/web/src/main/resources/static/sampleData/image/saekano.jpg"));
        File file = resource.getFile();

        AttachmentFile beforeAttachmentFile = new AttachmentFile(attachmentGroup);
        AttachmentFile newAttachmentFile = attachmentFileRepository.save(beforeAttachmentFile);
        String groupSn = StringUtils.padLeftUsingFormat(attachmentGroup.getId().toString(), 7, '0');
        String fileSn = StringUtils.padLeftUsingFormat(newAttachmentFile.getId().toString(), 7, '0');

        String filePath = FileUtils.getFilePath();
        String fileName = groupSn + fileSn;
        String fileExtension = "jpg";

        newAttachmentFile.setFileSize(file.length());
        newAttachmentFile.setOriginalFileName(resource.getFilename());
        newAttachmentFile.setFileName(fileName);
        newAttachmentFile.setPath(filePath);
        newAttachmentFile.setExtension(fileExtension);
        newAttachmentFile.setAttachmentFileType(AttachmentFileType.EVENT_BANNER);

        // 위치 폴더 생성
        FileUtils.createDir(downloadPath + filePath);
        try(
                BufferedInputStream inputStream = new BufferedInputStream(resource.getInputStream());
                BufferedOutputStream outputStream = new BufferedOutputStream(new FileOutputStream(newAttachmentFile.getFullPath(downloadPath)), 1024 * 500)
        ){
            // 파일 저장
            IOUtils.copy(inputStream, outputStream);
            outputStream.flush();
        } catch (IOException e) {
            // 로컬에 파일 저장에 실패했을 경우 기존 DB 정보도 삭제.
            attachmentFileRepository.delete(newAttachmentFile);
            throw new FileNotSavedException(e);
        }
    }
}

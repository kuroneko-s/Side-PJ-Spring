package com.choidh.service.main.service;

import com.choidh.service.account.entity.Account;
import com.choidh.service.account.service.AccountService;
import com.choidh.service.attachment.entity.AttachmentFile;
import com.choidh.service.attachment.service.AttachmentService;
import com.choidh.service.attachment.vo.AttachmentFileType;
import com.choidh.service.attachment.vo.ImageInfoVO;
import com.choidh.service.event.service.EventService;
import com.choidh.service.joinTables.entity.AccountTagJoinTable;
import com.choidh.service.learning.entity.Learning;
import com.choidh.service.learning.service.LearningService;
import com.choidh.service.main.vo.HomeVO;
import com.choidh.service.tag.entity.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import javax.annotation.Nullable;
import java.util.*;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class MainServiceImpl implements MainService {
    private final AccountService accountService;
    private final LearningService learningService;
    private final EventService eventService;
    private final AttachmentService attachmentService;

    /**
     * 홈 서비스
     */
    @Override
    public HomeVO getHomeVO(@Nullable Account account) {
        List<Learning> learningList;
        List<Learning> newLearningList;

        if (account != null) {
            account = accountService.getAccountByIdWithTags(account.getId());

            // 기본 강의 목록 보여주는 목록 로딩
            Set<AccountTagJoinTable> tags = account.getTags();
            if (tags.isEmpty()) {
                // 유저가 선호하는 태그가 없을 경우. (비로그인과 동일)
                learningList = learningService.getTop12LearningOrderByRating();
                newLearningList = learningService.getTop12LearningListByOpeningDate();
            } else {
                // 유저가 선호하는 태그가 있을 경우.
                Set<Tag> tagSet = tags.stream().map(AccountTagJoinTable::getTag).collect(Collectors.toSet());
                newLearningList = learningService.getTop12LearningListByTagsOrderByOpeningDate(tagSet);
                learningList = learningService.getTop12LearningListByTagsOrderByRating(tagSet);
            }
        } else {
            // 기본 강의 목록 보여주는 목록 로딩
            learningList = learningService.getTop12LearningOrderByRating();
            newLearningList = learningService.getTop12LearningListByOpeningDate();
        }

        // 이벤트 이미지 및 목록 로딩
        List<AttachmentFile> eventFileList = eventService.getEventFileList();

        // 강의 이미지 목록 조회
        Map<Long, ImageInfoVO> learningImageList = attachmentService.getImageInfo(
                learningList.stream().map(learning -> learning.getAttachmentGroup().getId()).collect(Collectors.toList()),
                AttachmentFileType.BANNER
        );

        // 신규 강의 이미지 목록 조회
        Map<Long, ImageInfoVO> newLearningImageList = attachmentService.getImageInfo(
                newLearningList.stream().map(learning -> learning.getAttachmentGroup().getId()).collect(Collectors.toList()),
                AttachmentFileType.BANNER
        );

        return HomeVO.builder()
                .eventFileList(eventFileList)
                .learningList(learningList)
                .newLearningList(newLearningList)
                .learningImageMap(learningImageList)
                .newLearningImageMap(newLearningImageList)
                .build();
    }
}

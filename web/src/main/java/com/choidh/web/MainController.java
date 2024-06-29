package com.choidh.web;

import com.choidh.service.account.entity.Account;
import com.choidh.service.account.service.AccountService;
import com.choidh.service.attachment.entity.AttachmentFile;
import com.choidh.service.attachment.entity.AttachmentFileType;
import com.choidh.service.attachment.service.AttachmentService;
import com.choidh.service.event.service.EventService;
import com.choidh.service.joinTables.entity.AccountTagJoinTable;
import com.choidh.service.learning.entity.Learning;
import com.choidh.service.learning.service.LearningService;
import com.choidh.service.tag.entity.Tag;
import com.choidh.web.common.annotation.CurrentAccount;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;

import java.util.*;
import java.util.stream.Collectors;

import static com.choidh.service.common.AppConstant.getTitle;

@Controller
@RequiredArgsConstructor
public class MainController {
    private final LearningService learningService;
    private final AccountService accountService;
    private final EventService eventService;
    private final AttachmentService attachmentService;

    @GetMapping("/")
    public String indexGet(@CurrentAccount Account account, Model model) {
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

            model.addAttribute(account);
        } else {
            // 기본 강의 목록 보여주는 목록 로딩
            learningList = learningService.getTop12LearningOrderByRating();
            newLearningList = learningService.getTop12LearningListByOpeningDate();
        }

        // 이벤트 이미지 및 목록 로딩
        List<AttachmentFile> eventFileList = eventService.getEventFileList();

        // 강의 이미지 목록 조회
        Map<Long, List<String>> learningImageMap = new HashMap<>();
        for (Learning learning : learningList) {
            List<AttachmentFile> attachmentFiles = attachmentService.getAttachmentFiles(learning.getAttachmentGroup().getId(), AttachmentFileType.BANNER);
            if (attachmentFiles.size() != 1) {
                throw new IllegalArgumentException();
            }

            List<String> valueList = learningImageMap.getOrDefault(learning.getId(), new ArrayList<>());

            AttachmentFile attachmentFile = attachmentFiles.get(0);
            valueList.add(attachmentFile.getFullPath(""));
            valueList.add(attachmentFile.getOriginalFileName());
            learningImageMap.put(learning.getId(), valueList);
        }

        Map<Long, List<String>> newLearningImageMap = new HashMap<>();
        for (Learning learning : newLearningList) {
            List<AttachmentFile> attachmentFiles = attachmentService.getAttachmentFiles(learning.getAttachmentGroup().getId(), AttachmentFileType.BANNER);
            if (attachmentFiles.size() != 1) {
                throw new IllegalArgumentException();
            }

            List<String> valueList = newLearningImageMap.getOrDefault(learning.getId(), new ArrayList<>());

            AttachmentFile attachmentFile = attachmentFiles.get(0);
            valueList.add(attachmentFile.getFullPath(""));
            valueList.add(attachmentFile.getOriginalFileName());
            newLearningImageMap.put(learning.getId(), valueList);
        }

        model.addAttribute("eventFileList", eventFileList);
        model.addAttribute("learningList", learningList);
        model.addAttribute("learningImageMap", learningImageMap);
        model.addAttribute("newLearningList", newLearningList);
        model.addAttribute("newLearningImageMap", newLearningImageMap);

        model.addAttribute("pageTitle", getTitle("홈"));

        return "home/index";
    }
}

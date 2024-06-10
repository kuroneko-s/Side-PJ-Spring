package com.choidh.web;

import com.choidh.service.account.entity.Account;
import com.choidh.service.attachment.entity.AttachmentFile;
import com.choidh.service.event.service.EventService;
import com.choidh.service.joinTables.entity.AccountTagJoinTable;
import com.choidh.service.learning.entity.Learning;
import com.choidh.service.learning.service.LearningService;
import com.choidh.service.menu.service.MenuTypeService;
import com.choidh.service.tag.entity.Tag;
import com.choidh.web.common.annotation.CurrentAccount;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;

import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

@Controller
@RequiredArgsConstructor
public class MainController {
    private final LearningService learningService;
    private final MenuTypeService menuTypeService;
    private final EventService eventService;

    @GetMapping("/")
    public String indexGet(@CurrentAccount Account account, Model model) {
        List<Learning> learningList;
        List<Learning> newLearningList;

        if (account != null) {
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

        model.addAttribute("eventFileList", eventFileList);
        model.addAttribute("learningList", learningList);
        model.addAttribute("newLearningList", newLearningList);

        return "index";
    }

    @PostMapping("/")
    public String indexPost(@CurrentAccount Account account, Model model) {
        if (account != null) model.addAttribute(account);

        return "index";
    }

    @GetMapping("/login")
    public String loginGet() {
        return "login";
    }
}

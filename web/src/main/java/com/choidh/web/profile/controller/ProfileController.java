package com.choidh.web.profile.controller;

import com.choidh.service.account.entity.Account;
import com.choidh.service.account.service.AccountService;
import com.choidh.service.account.vo.ModAccountVO;
import com.choidh.service.account.vo.ModNotificationVO;
import com.choidh.service.account.vo.ModPasswordVO;
import com.choidh.service.account.vo.ProfileVO;
import com.choidh.service.joinTables.entity.AccountTagJoinTable;
import com.choidh.service.joinTables.service.AccountTagService;
import com.choidh.service.learning.entity.Learning;
import com.choidh.service.learning.service.LearningService;
import com.choidh.service.notification.entity.Notification;
import com.choidh.service.notification.service.NotificationService;
import com.choidh.service.purchaseHistory.entity.PurchaseHistory;
import com.choidh.service.tag.service.TagService;
import com.choidh.web.common.annotation.CurrentAccount;
import com.choidh.web.profile.validator.ProfileNicknameValidator;
import com.choidh.web.profile.validator.ProfilePasswordValidator;
import com.choidh.web.profile.vo.NotificationUpdateForm;
import com.choidh.web.profile.vo.PasswordUpdateForm;
import com.choidh.web.profile.vo.ProfileUpdateForm;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.RequiredArgsConstructor;
import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.Errors;
import org.springframework.web.bind.WebDataBinder;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import javax.validation.Valid;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

import static com.choidh.service.common.AppConstant.getTitle;

@Controller
@RequestMapping(value = "/profile")
@RequiredArgsConstructor(onConstructor = @__(@Autowired))
public class ProfileController {
    private final ModelMapper modelMapper;
    private final ObjectMapper objectMapper;
    private final ProfileNicknameValidator profileNicknameValidator;
    private final ProfilePasswordValidator profilePasswordValidator;
    private final AccountService accountService;
    private final LearningService learningService;
    private final NotificationService notificationService;
    private final AccountTagService accountTagService;
    private final TagService tagService;

    public final static String CUSTOM_PROFILE = "profile/custom_profile";

    private static String redirectPath_Custom(Long id) {
        return "redirect:/profile/" + id + "/custom";
    }

    @InitBinder("profileUpdateForm")
    public void nicknameUpdate(WebDataBinder webDataBinder) {
        webDataBinder.addValidators(profileNicknameValidator);
    }

    @InitBinder("passwordUpdateForm")
    public void passwordUpdate(WebDataBinder webDataBinder) {
        webDataBinder.addValidators(profilePasswordValidator);
    }

    @GetMapping("/dashboard")
    public String getProfileView(@CurrentAccount Account account, Model model) {
        if (account == null) {
            throw new AccessDeniedException("접근 불가");
        }

        ProfileVO profileVO = accountService.getAccountForProfile(account.getId());
        account = profileVO.getAccount();

        model.addAttribute("accountVO", account);
        model.addAttribute("learningTitleList", profileVO.getLearningTitleList());
        model.addAttribute("questionList", profileVO.getQuestionList());
        model.addAttribute("tagTitleList", profileVO.getTagTitleList());
        model.addAttribute("learningTop4List", profileVO.getLearningTop4List());

        model.addAttribute("pageTitle", getTitle(account.getNickname()));
        model.addAttribute("pageContent", "profile/dashboard/contents");

        return "profile/index";
    }

    /**
     * GET 프로필 수정 화면
     */
    @GetMapping("/dashboard/custom")
    public String getAccountView(@CurrentAccount Account account, Model model) throws JsonProcessingException {
        Set<AccountTagJoinTable> tagListByAccountId = accountTagService.getTagListByAccountId(account.getId());
        List<String> tagtitleList = tagListByAccountId.stream().map(accountTagJoinTable -> accountTagJoinTable.getTag().getTitle()).collect(Collectors.toList());
        List<String> tagAllTitleList = tagService.getTitleList();

        model.addAttribute("account", account);
        model.addAttribute("tags", tagtitleList);
        model.addAttribute("whiteList", objectMapper.writeValueAsString(tagAllTitleList));
        model.addAttribute(new PasswordUpdateForm());
        model.addAttribute(modelMapper.map(account, ProfileUpdateForm.class));
        model.addAttribute(modelMapper.map(account, NotificationUpdateForm.class));

        return CUSTOM_PROFILE;
    }

    /**
     * PATCH 프로필 수정
     */
    @PatchMapping("/update/nickname/{id}")
    public String modAccount(@CurrentAccount Account account, @PathVariable Long id, Model model,
                             @Valid ProfileUpdateForm profileUpdateForm, Errors errors, RedirectAttributes attributes) {
        if (errors.hasErrors()) {
            model.addAttribute(account);
            model.addAttribute(new PasswordUpdateForm());
            model.addAttribute(modelMapper.map(account, NotificationUpdateForm.class));
            model.addAttribute("message", "잘못 입력하셧습니다. 다시 입력해주세요.");
            return CUSTOM_PROFILE;
        }

        account = accountService.modAccount(modelMapper.map(profileUpdateForm, ModAccountVO.class), account.getId());

        attributes.addFlashAttribute("account", account);
        attributes.addFlashAttribute("message", "프로필이 수정되었습니다.");

        return redirectPath_Custom(account.getId());
    }

    /**
     * PATCH 패스워드 수정
     */
    @PatchMapping("/update/password/{id}")
    public String modPassword(@CurrentAccount Account account, @PathVariable Long id,
                              @Valid PasswordUpdateForm passwordUpdateForm, Errors errors, Model model, RedirectAttributes attributes) {
        if (errors.hasErrors()) {
            model.addAttribute("account", account);
            model.addAttribute("profileUpdateForm", new ProfileUpdateForm());
            model.addAttribute(modelMapper.map(account, NotificationUpdateForm.class));
            model.addAttribute("message", "비밀번호가 잘못되었습니다. 다시 입력해주세요.");

            return CUSTOM_PROFILE;
        }

        account = accountService.modPassword(modelMapper.map(passwordUpdateForm, ModPasswordVO.class), account.getId());

        attributes.addFlashAttribute("account", account);
        attributes.addFlashAttribute("message", "비밀번호가 수정되었습니다.");

        return redirectPath_Custom(account.getId());
    }

    /**
     * PATCH 알림 설정 수정
     */
    @PatchMapping("/update/noti/{id}")
    public String modNotification(@CurrentAccount Account account, @PathVariable Long id,
                                  NotificationUpdateForm notificationUpdateForm, RedirectAttributes attributes) {
        account = accountService.modNotifications(modelMapper.map(notificationUpdateForm, ModNotificationVO.class), account.getId());

        attributes.addFlashAttribute("account", account);
        attributes.addFlashAttribute("message", "알림 설정이 완료되었습니다.");

        return redirectPath_Custom(account.getId());
    }

    /**
     * GET 프로필 알람 페이지
     */
    @GetMapping("/profile/notification")
    public String getNotificationView(@CurrentAccount Account account, Model model) {
        account = accountService.getAccountByIdWithPurchaseHistories(account.getId());
        List<Learning> learningList = account.getPurchaseHistories().stream().map(PurchaseHistory::getLearning).collect(Collectors.toList());
        // Set<Learning> learningList = learningService.getLearningList(account.getId());
        List<Notification> notificationList = notificationService.getNotificationListByType(
                learningList.stream().map(Learning::getId).collect(Collectors.toList())
        );

        model.addAttribute(account);
        model.addAttribute("notificationList", notificationList);

        return "profile/notification";
    }

    /**
     * 알람 비활성화. (관리자 전용)
     */
    @GetMapping("/profile/notification/remove")
    public String removeNotification(@RequestBody Long notificationId, RedirectAttributes attributes) {
        notificationService.delNotification(notificationId);

        attributes.addFlashAttribute("message", "알림이 삭제되었습니다.");
        return "redirect:/profile/notification";
    }

    /**
     * 프로필 내 학습 화면
     */
    @GetMapping("/profile/learning")
    public String getProfileLearningView(@CurrentAccount Account account, Model model) {
        Set<Learning> learningList = learningService.getLearningList(account.getId());

        model.addAttribute("account", account);
        model.addAttribute("learningList", learningList);
        model.addAttribute("now", LocalDateTime.now().minusDays(3));

        return "profile/learning";
    }
}

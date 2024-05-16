package com.choidh.web.profile.controller;

import com.choidh.service.account.entity.Account;
import com.choidh.service.account.repository.AccountRepository;
import com.choidh.service.learning.entity.Learning;
import com.choidh.service.notification.entity.Notification;
import com.choidh.service.notification.repository.NotificationRepository;
import com.choidh.service.question.entity.Question;
import com.choidh.service.tag.entity.Tag;
import com.choidh.service.tag.repository.TagRepository;
import com.choidh.web.account.service.AccountServiceImpl;
import com.choidh.web.notification.service.NotificationService;
import com.choidh.web.profile.validator.ProfileNicknameValidator;
import com.choidh.web.profile.validator.ProfilePasswordValidator;
import com.choidh.web.profile.vo.NotificationUpdateForm;
import com.choidh.web.profile.vo.PasswordUpdateForm;
import com.choidh.web.profile.vo.ProfileUpdateForm;
import com.choidh.web.common.annotation.CurrentAccount;
import com.choidh.web.tag.vo.TagForm;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.RequiredArgsConstructor;
import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
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

@Controller
@RequiredArgsConstructor(onConstructor = @__(@Autowired))
public class ProfileController {
    private final ProfileNicknameValidator profileNicknameValidator;
    private final AccountServiceImpl accountServiceImpl;
    private final ModelMapper modelMapper;
    private final ProfilePasswordValidator profilePasswordValidator;
    private final TagRepository tagRepository;
    private final ObjectMapper objectMapper;
    private final NotificationRepository notificationRepository;
    private final NotificationService notificationService;
    private final AccountRepository accountRepository;

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

    @GetMapping("/profile/{id}")
    public String viewProfile(@CurrentAccount Account account, @PathVariable Long id, Model model) {
        Account newAccount = accountRepository.findAccountWithAll(id);
        List<String> learningTitle = newAccount.getListenLearning().stream().map(Learning::getTitle).limit(3).collect(Collectors.toList());
        List<Question> accountQuestion = newAccount.getQuestions().stream().limit(4).collect(Collectors.toList());
        List<String> tagList = newAccount.getTags().stream().map(Tag::getTitle).collect(Collectors.toList());
        List<Learning> learnings = newAccount.getLearnings().stream().limit(4).collect(Collectors.toList());

        model.addAttribute("account", newAccount);
        model.addAttribute("learningTitle", learningTitle);
        model.addAttribute("accountQuestion", accountQuestion);
        model.addAttribute("tagList", tagList);
        model.addAttribute("learnings", learnings);

        return "profile/profile";
    }

    @GetMapping("/profile/{id}/custom")
    public String viewRevise(@CurrentAccount Account account, @PathVariable Long id, Model model) throws JsonProcessingException {
        Set<Tag> tags = accountServiceImpl.getTags(account);
        List<String> tagList = tagRepository.findAll().stream().map(Tag::getTitle).collect(Collectors.toList());

        model.addAttribute(account);
        model.addAttribute("tags", tags.stream().map(Tag::getTitle).collect(Collectors.toList()));
        model.addAttribute("whiteList", objectMapper.writeValueAsString(tagList));
        model.addAttribute(modelMapper.map(account, ProfileUpdateForm.class));
        model.addAttribute(new PasswordUpdateForm());
        model.addAttribute(modelMapper.map(account, NotificationUpdateForm.class));

        return CUSTOM_PROFILE;
    }

    /**
     * 프로필 수정
     */
    @PostMapping("/update/nickname/{id}")
    public String updateNicknameForm(@CurrentAccount Account account, @PathVariable Long id, Model model,
                                     @Valid ProfileUpdateForm profileUpdateForm, Errors errors, RedirectAttributes attributes) {
        if (errors.hasErrors()) {
            model.addAttribute(account);
            model.addAttribute(new PasswordUpdateForm());
            model.addAttribute(modelMapper.map(account, NotificationUpdateForm.class));
            model.addAttribute("message", "잘못 입력하셧습니다. 다시 입력해주세요.");
            return CUSTOM_PROFILE;
        }

        Account newAccount = accountServiceImpl.updateNicknameAndDescription(profileUpdateForm, account);

        attributes.addFlashAttribute("account", newAccount);
        attributes.addFlashAttribute("message", "프로필이 수정되었습니다.");
        return redirectPath_Custom(newAccount.getId());
    }

    /**
     * 패스워드 수정
     */
    @PostMapping("/update/password/{id}")
    public String updatePasswordForm(@CurrentAccount Account account, @PathVariable Long id,
                                     @Valid PasswordUpdateForm passwordUpdateForm, Errors errors, Model model, RedirectAttributes attributes) {
        if (errors.hasErrors()) {
            model.addAttribute(account);
            model.addAttribute(new ProfileUpdateForm());
            model.addAttribute(modelMapper.map(account, NotificationUpdateForm.class));
            model.addAttribute("message", "비밀번호가 잘못되었습니다. 다시 입력해주세요.");

            return CUSTOM_PROFILE;
        }

        final Account newAccount = accountServiceImpl.updatePassword(passwordUpdateForm, account);

        attributes.addFlashAttribute("account", newAccount);
        attributes.addFlashAttribute("message", "비밀번호가 수정되었습니다.");

        return redirectPath_Custom(account.getId());
    }

    @PostMapping("/update/noti/{id}")
    public String updateNotificationForm(@CurrentAccount Account account, @PathVariable Long id, Model model,
                                         NotificationUpdateForm notificationUpdateForm, RedirectAttributes attributes) {
        Account newAccount = accountServiceImpl.updateNotifications(notificationUpdateForm, account);

        attributes.addFlashAttribute("account", newAccount);
        attributes.addFlashAttribute("message", "알림 설정이 완료되었습니다.");
        return redirectPath_Custom(account.getId());
    }

    @PostMapping("/update/tags/add")
    public @ResponseBody ResponseEntity addTag(@CurrentAccount Account account, @RequestBody TagForm tagForm) {
        Tag tag = tagRepository.findByTitle(tagForm.getTitle());
        if (tag == null) {
            tag = tagRepository.save(Tag.builder()
                    .title(tagForm.getTitle())
                    .build());
        }

        accountServiceImpl.addTag(account, tag);
        return ResponseEntity.ok().build();
    }

    @PostMapping("/update/tags/remove")
    public @ResponseBody ResponseEntity removeTag(@CurrentAccount Account account, @RequestBody TagForm tagForm) {
        Tag tag = tagRepository.findByTitle(tagForm.getTitle());

        if (tag == null) {
            return ResponseEntity.badRequest().build();
        }

        accountServiceImpl.deleteTag(account, tag);
        return ResponseEntity.ok().build();
    }

    @GetMapping("/profile/notification")
    public String viewNotification(@CurrentAccount Account account, Model model) {
        List<Notification> notCheckedNotifications = notificationRepository.findByAccountAndChecked(account, false);
        List<Notification> checkedNotifications = notificationRepository.findByAccountAndChecked(account, true);

        model.addAttribute(account);
        model.addAttribute("notChecked", notCheckedNotifications);
        model.addAttribute("checked", checkedNotifications);

        notificationService.readNotifications(notCheckedNotifications);

        return "profile/notification";
    }

    @GetMapping("/profile/notification/remove")
    public String removeNotification(@CurrentAccount Account account, RedirectAttributes attributes) {

        notificationService.deleteNotifications(account);

        attributes.addFlashAttribute("message", "알림이 삭제되었습니다.");
        return "redirect:/profile/notification";
    }

    @GetMapping("/profile/learning")
    public String viewLearning(@CurrentAccount Account account, Model model) {
        Account newAccount = accountRepository.findById(account.getId()).orElseThrow();
        Set<Learning> listenLearning = newAccount.getListenLearning();

        model.addAttribute("account", account);
        model.addAttribute("learningList", listenLearning);
        model.addAttribute("now", LocalDateTime.now().minusDays(3));

        return "profile/learning";
    }
}

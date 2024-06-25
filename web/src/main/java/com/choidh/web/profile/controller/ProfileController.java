package com.choidh.web.profile.controller;

import com.choidh.service.account.entity.Account;
import com.choidh.service.account.service.AccountService;
import com.choidh.service.account.vo.ModAccountVO;
import com.choidh.service.account.vo.ModNotificationVO;
import com.choidh.service.account.vo.ModPasswordVO;
import com.choidh.service.account.vo.ProfileVO;
import com.choidh.service.common.StringUtils;
import com.choidh.service.joinTables.entity.AccountTagJoinTable;
import com.choidh.service.joinTables.service.AccountTagService;
import com.choidh.service.learning.entity.Learning;
import com.choidh.service.learning.service.LearningService;
import com.choidh.service.notification.entity.Notification;
import com.choidh.service.notification.service.NotificationService;
import com.choidh.service.purchaseHistory.entity.PurchaseHistory;
import com.choidh.service.security.AccountUser;
import com.choidh.service.tag.service.TagService;
import com.choidh.service.tag.vo.RegTagVO;
import com.choidh.web.common.annotation.CurrentAccount;
import com.choidh.web.profile.validator.ProfileNicknameValidator;
import com.choidh.web.profile.validator.ProfilePasswordValidator;
import com.choidh.web.profile.vo.NotificationUpdateVO;
import com.choidh.web.profile.vo.PasswordUpdateVO;
import com.choidh.web.profile.vo.ProfileUpdateVO;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.RequiredArgsConstructor;
import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.context.SecurityContext;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.Errors;
import org.springframework.web.bind.WebDataBinder;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import javax.validation.Valid;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;
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

    @InitBinder("profileUpdateVO")
    public void nicknameUpdate(WebDataBinder webDataBinder) {
        webDataBinder.addValidators(profileNicknameValidator);
    }

    @InitBinder("passwordUpdateVO")
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
     * GET 프로필 알림 페이지
     */
    @GetMapping("/notification")
    public String getNotificationView(@CurrentAccount Account account, Model model) {
        if (account == null) {
            throw new AccessDeniedException("접근 불가");
        }

        account = accountService.getAccountByIdWithPurchaseHistories(account.getId());
        List<Learning> learningList = account.getPurchaseHistories().stream().map(PurchaseHistory::getLearning).collect(Collectors.toList());
        List<Notification> notificationList = notificationService.getNotificationListByType(
                learningList.stream().map(Learning::getId).collect(Collectors.toList())
        );

        model.addAttribute("notificationList", notificationList);

        model.addAttribute("pageTitle", getTitle("알림"));
        model.addAttribute("pageContent", "profile/notification/contents");

        return "profile/index";
    }

    /**
     * POST 알림 비활성화. API.
     */
    @PostMapping("/notification")
    public String removeNotification(@RequestBody Long notificationId, RedirectAttributes attributes) {
        notificationService.delNotification(notificationId);

        attributes.addFlashAttribute("message", "알림이 삭제되었습니다.");
        return "redirect:/profile/notification";
    }

    /**
     * 프로필 내 학습 화면
     */
    @GetMapping("/learning")
    public String getProfileLearningView(@CurrentAccount Account account, Model model) {
        if (account == null) {
            throw new AccessDeniedException("접근 불가");
        }

        Set<Learning> learningList = learningService.getLearningList(account.getId());

        model.addAttribute("learningList", learningList);
        model.addAttribute("now", LocalDateTime.now().minusDays(3));

        model.addAttribute("pageTitle", getTitle("알림"));
        model.addAttribute("pageContent", "profile/learning/contents");

        return "profile/index";
    }

    /**
     * GET 프로필 수정 화면
     */
    @GetMapping("/setting")
    public String getAccountView(@CurrentAccount Account account, Model model) throws JsonProcessingException {
        if (account == null) {
            throw new AccessDeniedException("접근 불가");
        }

        Set<AccountTagJoinTable> tagListByAccountId = accountTagService.getTagListByAccountId(account.getId());
        List<String> tagtitleList = tagListByAccountId.stream().map(accountTagJoinTable -> accountTagJoinTable.getTag().getTitle()).collect(Collectors.toList());
        List<String> tagAllTitleList = tagService.getTitleList();

        model.addAttribute("tags", tagtitleList);
        model.addAttribute("whiteList", objectMapper.writeValueAsString(tagAllTitleList));
        model.addAttribute("tagList", objectMapper.writeValueAsString(tagtitleList));

        model.addAttribute("pageTitle", getTitle("설정"));
        model.addAttribute("pageContent", "profile/setting/contents");

        return "profile/index";
    }

    /**
     * PATCH 프로필 수정. API
     */
    @PatchMapping("/setting/profile")
    public ResponseEntity modAccount(@CurrentAccount Account account, @Valid @RequestBody ProfileUpdateVO profileUpdateVO, Errors errors) {
        if (errors.hasFieldErrors("nickname")) {
            return ResponseEntity.badRequest().body("이미 존재하는 닉네임이에요. 다른 닉네임을 입력해주세요!");
        }
        if (errors.hasErrors()) {
            return ResponseEntity.badRequest().body("잘못 입력하셧습니다. 다시 입력해주세요.");
        }

        accountService.modAccount(modelMapper.map(profileUpdateVO, ModAccountVO.class), account.getId());

        // Principal 갱신
        this.refreshPrincipal(account.getId());

        return ResponseEntity.ok("프로필이 수정되었습니다.");
    }

    /**
     * PATCH 패스워드 수정. API
     */
    @PatchMapping("/setting/password")
    public ResponseEntity modPassword(@CurrentAccount Account account, @Valid @RequestBody PasswordUpdateVO passwordUpdateVO, Errors errors) {
        if (errors.hasErrors()) {
            return ResponseEntity.badRequest().body("비밀번호가 잘못되었습니다. 다시 입력해주세요.");
        }

        accountService.modPassword(modelMapper.map(passwordUpdateVO, ModPasswordVO.class), account.getId());

        return ResponseEntity.ok("비밀번호가 수정되었습니다.");
    }

    /**
     * PATCH 알림 설정 수정. API
     */
    @PatchMapping("/setting/notification")
    public ResponseEntity modNotification(@CurrentAccount Account account, @RequestBody NotificationUpdateVO notificationUpdateVO) {
        accountService.modNotifications(modelMapper.map(notificationUpdateVO, ModNotificationVO.class), account.getId());

        // Principal 갱신
        this.refreshPrincipal(account.getId());

        return ResponseEntity.ok("알림 설정이 완료되었습니다.");
    }

    /**
     * POST 계정 태그 추가
     */
    @PostMapping("/tag/add")
    public ResponseEntity addTag(@CurrentAccount Account account, @RequestBody Map<String, Object> params) {
        if (!params.containsKey("context")) {
            return ResponseEntity.badRequest().build();
        }

        String context = params.get("context").toString();
        if (StringUtils.isNullOrEmpty(context)) {
            return ResponseEntity.badRequest().build();
        }

        AccountTagJoinTable accountTagJoinTable = accountTagService.regTag(
                RegTagVO.builder()
                        .title(context)
                        .build(),
                account.getId());

        if (accountTagJoinTable == null) {
            return ResponseEntity.badRequest().build();
        }

        return ResponseEntity.ok().build();
    }

    /**
     * POST 계정 태그 삭제
     */
    @PostMapping("/tag/remove")
    public ResponseEntity removeTag(@CurrentAccount Account account, @RequestBody Map<String, Object> params) {
        if (!params.containsKey("context")) {
            return ResponseEntity.badRequest().build();
        }

        String context = params.get("context").toString();
        if (StringUtils.isNullOrEmpty(context)) {
            return ResponseEntity.badRequest().build();
        }

        if (accountTagService.delTag(context, account.getId())) {
            return ResponseEntity.ok().build();
        } else {
            return ResponseEntity.badRequest().build();
        }
    }

    private void refreshPrincipal(Long accountId) {
        Account account = accountService.getAccountById(accountId);

        // 로그인 성공했다는 유효 토큰 생성.
        UsernamePasswordAuthenticationToken token = new UsernamePasswordAuthenticationToken(
                new AccountUser(account),
                account.getPassword(),
                List.of(new SimpleGrantedAuthority("ROLE_USER"))
        );
        SecurityContext context = SecurityContextHolder.getContext();
        context.setAuthentication(token); // 토큰을 Thread Holder 에 저장.
    }
}

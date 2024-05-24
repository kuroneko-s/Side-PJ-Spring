package com.choidh.web.account.service;


import com.choidh.service.account.entity.Account;
import com.choidh.service.account.repository.AccountRepository;
import com.choidh.service.learning.entity.Learning;
import com.choidh.service.mail.service.EmailService;
import com.choidh.service.mail.vo.EmailCheckMessageVO;
import com.choidh.service.tag.entity.Tag;
import com.choidh.web.account.vo.AccountVO;
import com.choidh.web.account.vo.EmailTokenVO;
import com.choidh.web.profile.vo.NotificationUpdateForm;
import com.choidh.web.profile.vo.PasswordUpdateForm;
import com.choidh.web.profile.vo.ProfileUpdateForm;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.ui.Model;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.util.List;
import java.util.Optional;
import java.util.Set;

import static com.choidh.service.common.AppConstant.getAccountNotFoundErrorMessage;

/**
 * Account 비지니스 서비스 로직
 */

@Slf4j
@Service
@Transactional(readOnly = true)
@RequiredArgsConstructor(onConstructor = @__(@Autowired))
public class AccountServiceImpl implements AccountService {
    private final AccountRepository accountRepository;
    private final PasswordEncoder passwordEncoder;
    private final EmailService emailService;
    private final ModelMapper modelMapper;

    /**
     * Account 신규 생성.
     */
    @Override
    @Transactional
    public void postCreateAccount(AccountVO accountVO) {
        this.createAccount(modelMapper.map(accountVO, Account.class));
    }

    /**
     * 이메일 인증 화면
     * @return 보여줄 화면의 경로
     */
    @Override
    @Transactional
    public String getMailAuthentication(String token, String email, Model model, RedirectAttributes attributes) {
        Account account = accountRepository.findByEmailAndTokenChecked(email, false);

        if (account == null || !account.getEmailCheckToken().equals(token)) {
            model.addAttribute("message", "인증 주소가 잘못 되었습니다. 다시 신청해주시거나, 재전송 버튼을 눌러주세요");
            model.addAttribute("email", email);
            model.addAttribute(new EmailTokenVO());

            return "navbar/token_validation";
        }

        account.setTokenChecked(true);
        this.login(account);

        attributes.addFlashAttribute("account", account);
        attributes.addFlashAttribute("success", "인증이 완료되었습니다.");

        return "redirect:/";
    }

    /**
     * 이메일 인증 재요청
     */
    @Override
    public String postMailAuthenticationRetry(String email, Model model, RedirectAttributes attributes) {
        if (email == null || email.isEmpty()) {
            model.addAttribute("message", "메일이 옳바르지 않습니다. 다시 입력해주세요.");

            return "registerSuccess";
        }

        Account account = accountRepository.findByEmailAndTokenChecked(email, false);
        if (account != null && account.canCheckingEmailToken()) {
            // 이메일 재전송 요청
            emailService.sendCheckEmail(EmailCheckMessageVO.getInstance(account));
            attributes.addFlashAttribute("message", "인증용 메일이 전송 되었습니다. 확인해주세요");
        } else {
            model.addAttribute("message", "인증용 메일은 1시간에 한번만 전송이 가능합니다. 양해 부탁드립니다");
        }

        return "registerSuccess";
    }

    /**
     * Account 생성
     */
    @Override
    @Transactional
    public Account createAccount(Account account) {
        account.setPassword(passwordEncoder.encode(account.getPassword()));
        account.setTokenChecked(false);

        // 메일 전송
        account.createEmailCheckToken();
        emailService.sendCheckEmail(EmailCheckMessageVO.getInstance(account));

        return accountRepository.save(account);
    }

    /**
     * Account 이메일 인증 성공 처리 및 로그인 처리
     */
    @Override
    @Transactional
    public void createAccountToken(Account account) {
        account.setTokenChecked(true);
        accountRepository.save(account);

        this.login(account);
    }

    /**
     * 이메일 인증 미처리 Account 조회 by Email
     */
    @Override
    public Account findAccountByEmailWithNotChecking(String email) {
        return accountRepository.findByEmailAndTokenChecked(email, false);
    }

    /**
     * 인증용 이메일 재전송
     */
    @Override
    public void reCheckingEmailToken(Account account) {
        emailService.sendCheckEmail(EmailCheckMessageVO.getInstance(account));
    }

    /**
     * 닉네임 및 개인소개 수정
     */
    @Override
    @Transactional
    public Account updateNicknameAndDescription(ProfileUpdateForm profileUpdateForm, Account account) {
        if (profileUpdateForm.getNickname() != null && !profileUpdateForm.getNickname().isEmpty())
            account.setNickname(profileUpdateForm.getNickname());

        if (profileUpdateForm.getDescription() != null) account.setDescription(profileUpdateForm.getDescription());

        // 영관계 관리에서 벗어나있으니 수동으로 수정.
        return accountRepository.save(account);
    }

    /**
     * 패스워드 수정
     */
    @Override
    @Transactional
    public Account updatePassword(PasswordUpdateForm passwordUpdateForm, Account account) {
        account.setPassword(passwordEncoder.encode(passwordUpdateForm.getNewPassword()));

        return accountRepository.save(account);
    }

    /**
     * 알림 설정 변경
     */
    @Override
    @Transactional
    public Account updateNotifications(NotificationUpdateForm notificationUpdateForm, Account account) {
        modelMapper.map(notificationUpdateForm, account);

        return accountRepository.save(account);
    }

    /**
     * 계정에 태그 추가
     */
    @Override
    @Transactional
    public void addTag(Account account, Tag tag) {
        Optional<Account> accountById = accountRepository.findById(account.getId());
        accountById.ifPresent(a -> a.getTags().add(tag));
    }

    /**
     * 계정의 태그 목록조회
     */
    @Override
    public Set<Tag> getTags(Account account) {
        return accountRepository.findById(account.getId())
                .orElseThrow(() -> new IllegalArgumentException(getAccountNotFoundErrorMessage(account.getId())))
                .getTags();
    }

    /**
     * 계정의 태그 삭제
     */
    @Override
    @Transactional
    public void deleteTag(Account account, Tag tag) {
        accountRepository.findById(account.getId())
                .orElseThrow(() -> new IllegalArgumentException(getAccountNotFoundErrorMessage(account.getId())))
                .getTags().remove(tag);
    }

    /**
     * 강의를 카트에 추가
     */
    @Override
    @Transactional
    public void addLearningInCart(Account newAccount, Learning learning) {
        newAccount.getCartList().add(learning);
    }

    /**
     * 강의 구매 처리
     */
    @Override
    @Transactional
    public Account buyLearningSuccessful(Long accountId, List<Learning> learningList) {
        Account account = accountRepository.findById(accountId)
                .orElseThrow(() -> new IllegalArgumentException(getAccountNotFoundErrorMessage(accountId)));

        // 강의별로 구매 처리.
        for (Learning learning : learningList) {
            account.buyLearning(learning);
            learning.buyLearning(account);
        }

        return account;
    }

    /**
     * 강의 구매 취소 처리
     */
    @Override
    @Transactional
    public Account removeListenLearning(Long accountId, List<Learning> learningList) {
        Account account = accountRepository.findById(accountId)
                .orElseThrow(() -> new IllegalArgumentException(getAccountNotFoundErrorMessage(accountId)));

        // 구매 취소 처리
        for (Learning learning : learningList) {
            account.cancelLearning(learning);
            learning.cancelLearning(learning);
        }

        return account;
    }
}

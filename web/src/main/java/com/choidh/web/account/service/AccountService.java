package com.choidh.web.account.service;

import com.choidh.service.account.entity.Account;
import com.choidh.service.security.AccountUser;
import com.choidh.service.learning.entity.Learning;
import com.choidh.service.tag.entity.Tag;
import com.choidh.web.account.vo.AccountVO;
import com.choidh.web.profile.vo.NotificationUpdateForm;
import com.choidh.web.profile.vo.PasswordUpdateForm;
import com.choidh.web.profile.vo.ProfileUpdateForm;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.context.SecurityContext;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.ui.Model;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.util.List;
import java.util.Set;

/**
 * Account 비지니스 서비스
 */

public interface AccountService {
    // ################# 단순 컨트롤러 역할의 위임 #################
    /**
     * Post 회원가입 처리
     */
    void postCreateAccount(Model model, AccountVO accountVO);

    /**
     * 이메일 인증 화면
     * @return 보여줄 화면의 경로
     */
    String getMailAuthentication(String token, String email, Model model, RedirectAttributes attributes);

    /**
     * 이메일 인증 재요청
     */
    String postMailAuthenticationRetry(String email, Model model, RedirectAttributes attributes);

    // ################# 기능 서비스 #################

    // 강제 로그인 처리.
    default void login(Account account) {
        // 로그인 성공했다는 유효 토큰 생성.
        UsernamePasswordAuthenticationToken token = new UsernamePasswordAuthenticationToken(
                new AccountUser(account),
                account.getPassword(),
                List.of(new SimpleGrantedAuthority("ROLE_USER"))
        );
        SecurityContext context = SecurityContextHolder.getContext();
        context.setAuthentication(token); // 토큰을 Thread Holder 에 저장.
    }

    /**
     * Account 생성
     */
    Account createAccount(Account account);

    /**
     * Account 이메일 인증 성공 처리 및 로그인 처리
     */
    void createAccountToken(Account account);

    /**
     * 이메일 인증 미처리 Account 조회 by Email
     */
    Account findAccountByEmailWithNotChecking(String email);

    /**
     * 인증용 이메일 재전송
     */
    void reCheckingEmailToken(Account account);

    /**
     * 닉네임 및 개인소개 수정
     */
    Account updateNicknameAndDescription(ProfileUpdateForm profileUpdateForm, Account account);

    /**
     * 패스워드 수정
     */
    Account updatePassword(PasswordUpdateForm passwordUpdateForm, Account account);

    /**
     * 알림 설정 변경
     */
    Account updateNotifications(NotificationUpdateForm notificationUpdateForm, Account account);

    /**
     * 계정에 태그 추가
     */
    void addTag(Account account, Tag tag);

    /**
     * 계정의 태그 목록조회
     */
    Set<Tag> getTags(Account account);

    /**
     * 계정의 태그 삭제
     */
    void deleteTag(Account account, Tag tag);

    /**
     * 강의를 카트에 추가
     */
    void addLearningInCart(Account newAccount, Learning learning);

    /**
     * 강의 구매 처리
     */
    Account buyLearningSuccessful(Long accountId, List<Learning> learningList);

    /**
     * 강의 구매 취소 처리
     */
    Account removeListenLearning(Long accountId, List<Learning> learningList);
}

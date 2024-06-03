package com.choidh.service.account.service;

import com.choidh.service.account.entity.Account;
import com.choidh.service.account.vo.ModNotificationVO;
import com.choidh.service.account.vo.ModPasswordVO;
import com.choidh.service.account.vo.ModProfileVO;
import com.choidh.service.account.vo.RegAccountVO;
import com.choidh.service.kakao.vo.KakaoPayCancelResult;
import com.choidh.service.kakao.vo.KakaoPaySuccessResult;
import com.choidh.service.security.AccountUser;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.context.SecurityContext;
import org.springframework.security.core.context.SecurityContextHolder;

import java.util.List;

/**
 * Account 비지니스 서비스
 */

public interface AccountService {
    /**
     * 강제 로그인 처리.
     */
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
     * Get Account 단건 조회 By Id
     */
    Account getAccountById(Long accountId);

    /**
     * Get Account 단건 조회 By Id With Cart
     */
    Account getAccountByIdWithCart(Long accountId);

    /**
     * Reg Account 생성
     */
    Account regAccount(RegAccountVO regAccountVO);

    /**
     * 인증용 이메일 전송
     */
    boolean sendEmailForAuthentication(String email);

    /**
     * 이메일 인증
     */
    boolean verifyingEmail(String token, String email);

    /**
     * Mod 프로필 수정
     */
    Account modAccount(ModProfileVO modProfileVO, Long accountId);

    /**
     * Mod 패스워드 수정
     */
    Account modPassword(ModPasswordVO modPasswordVO, Long accountId);

    /**
     * mod 알림 설정 수정
     */
    Account modNotifications(ModNotificationVO modNotificationVO, Long accountId);

    /**
     * GET 유저 단건 조회 For 프로필 화면용
     */
    Account getAccountForProfile(Long accountId);
}
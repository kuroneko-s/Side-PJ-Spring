package com.choidh.service.account.service;

import com.choidh.service.account.entity.Account;
import com.choidh.service.account.vo.*;

/**
 * Account 비지니스 서비스
 */

public interface AccountService {
    /**
     * Account 단건 조회 By Id
     */
    Account getAccountById(Long accountId);

    /**
     * Account 단건 조회 By Email
     */
    Account getAccountByEmail(String email);

    /**
     * Account 단건 조회 By Email And Email Checked Token
     */
    Account getAccountByEmailAndChecked(String email, boolean checked);

    /**
     * Account 단건 조회 By Id With Tags
     */
    Account getAccountByIdWithTags(Long accountId);

    /**
     * Account 단건 조회. By Id With Learning In Cart
     */
    Account getAccountByIdWithLearningInCart(Long accountId);

    /**
     * Account 단건 조회 By Id With PurchaseHistories
     */
    Account getAccountByIdWithPurchaseHistories(Long accountId);

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
    Account modAccount(ModAccountVO modAccountVO, Long accountId);

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
    ProfileVO getAccountForProfile(Long accountId);

    /**
     * Chk 해당 강의를 수강중인 학생인지 검증
     */
    void chkAccountHasLearning(Long accountId, Long learningId);
}

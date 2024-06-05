package com.choidh.service.account.service;


import com.choidh.service.account.entity.Account;
import com.choidh.service.account.repository.AccountRepository;
import com.choidh.service.account.vo.ModAccountVO;
import com.choidh.service.account.vo.ModNotificationVO;
import com.choidh.service.account.vo.ModPasswordVO;
import com.choidh.service.account.vo.RegAccountVO;
import com.choidh.service.cart.entity.Cart;
import com.choidh.service.cart.service.CartService;
import com.choidh.service.joinTables.entity.LearningCartJoinTable;
import com.choidh.service.joinTables.service.LearningCartService;
import com.choidh.service.mail.service.EmailService;
import com.choidh.service.mail.vo.EmailForAuthenticationVO;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Set;

import static com.choidh.service.common.AppConstant.getAccountNotFoundErrorMessage;

@Slf4j
@Service
@Transactional(readOnly = true)
@RequiredArgsConstructor(onConstructor = @__(@Autowired))
public class AccountServiceImpl implements AccountService {
    private final ModelMapper modelMapper;
    private final PasswordEncoder passwordEncoder;
    private final AccountRepository accountRepository;
    private final EmailService emailService;
    private final LearningCartService learningCartService;
    private final CartService cartService;

    /**
     * Account 단건 조회 By Id
     */
    @Override
    public Account getAccountById(Long accountId) {
        return accountRepository.findById(accountId)
                .orElseThrow(() -> new IllegalArgumentException(getAccountNotFoundErrorMessage(accountId)));
    }

    /**
     * Account 단건 조회. By Id With Learning In Cart
     */
    @Override
    public Account getAccountByIdWithLearningInCart(Long accountId) {
        // Account 단건 조회. By Id With LearningCartJoinTable
        Account account = accountRepository.findAccountByIdWithLearningCart(accountId);

        if (account.getCart() == null) {
            Cart cart = cartService.regCart(accountId);
            account.setCart(cart);
        }

        // LearningCartJoinTable 목록 조회. By Cart Id
        Set<LearningCartJoinTable> cartListWithLearningByCartId = learningCartService.getCartListWithLearningByCartId(account.getCart().getId());
        // 조회 결과를 Account.Cart 에 덮어쓰기
        account.getCart().setLearningCartJoinTables(cartListWithLearningByCartId);

        return account;
    }

    /**
     * Account 단건 조회 By Id With PurchaseHistories Learning
     */
    @Override
    public Account getAccountByIdWithPurchaseHistories(Long accountId) {
        Account account = accountRepository.findAccountWithPurchaseHistories(accountId);

        if (account.getCart() == null) {
            Cart cart = cartService.regCart(accountId);
            account.setCart(cart);
        }

        return account;
    }

    /**
     * Reg Account 생성
     */
    @Override
    @Transactional
    public Account regAccount(RegAccountVO regAccountVO) {
        Account account = modelMapper.map(regAccountVO, Account.class);

        account.setPassword(passwordEncoder.encode(account.getPassword()));
        account.setChecked(false);

        // 메일 전송
        account.createTokenForEmailForAuthentication();
        emailService.sendEmailForAuthentication(EmailForAuthenticationVO.getInstance(account));

        return accountRepository.save(account);
    }

    /**
     * 인증용 이메일 전송
     */
    @Override
    public boolean sendEmailForAuthentication(String email) {
        Account account = accountRepository.findByEmailAndChecked(email, false);

        if (account != null && account.canCheckingEmailToken()) {
            // 인증용 이메일 전송
            emailService.sendEmailForAuthentication(EmailForAuthenticationVO.getInstance(account));
            return true;
        } else {
            return false;
        }
    }

    /**
     * 이메일 인증
     */
    @Override
    @Transactional
    public boolean verifyingEmail(String token, String email) {
        Account account = accountRepository.findByEmailAndChecked(email, false);

        if (account == null || !account.getTokenForEmailForAuthentication().equals(token)) {
            return false;
        }

        account.setChecked(true);
        this.login(account);

        return false;
    }

    /**
     * Mod 프로필 수정
     */
    @Override
    @Transactional
    public Account modAccount(ModAccountVO modAccountVO, Long accountId) {
        Account account = this.getAccountById(accountId);

        if (modAccountVO.getNickname() != null && !modAccountVO.getNickname().isEmpty()) account.setNickname(modAccountVO.getNickname());
        if (modAccountVO.getDescription() != null) account.setDescription(modAccountVO.getDescription());

        return account;
    }

    /**
     * Mod 패스워드 수정
     */
    @Override
    @Transactional
    public Account modPassword(ModPasswordVO modPasswordVO, Long accountId) {
        Account account = this.getAccountById(accountId);

        System.out.println(passwordEncoder.matches(modPasswordVO.getNowPassword(), account.getPassword()));
        System.out.println(modPasswordVO.getNewPassword().equals(modPasswordVO.getNewPasswordCheck()));

        if (
                !passwordEncoder.matches(modPasswordVO.getNowPassword(), account.getPassword())
                        || !modPasswordVO.getNewPassword().equals(modPasswordVO.getNewPasswordCheck())
        ) {
            throw new IllegalArgumentException("패스워드가 일치하지 않음");
        }
        account.setPassword(passwordEncoder.encode(modPasswordVO.getNewPassword()));

        return account;
    }

    /**
     * Mod 알림 설정 수정
     */
    @Override
    @Transactional
    public Account modNotifications(ModNotificationVO modNotificationVO, Long accountId) {
        Account account = this.getAccountById(accountId);
        account.setSiteMailNotification(modNotificationVO.isSiteMailNotification());
        account.setSiteWebNotification(modNotificationVO.isSiteWebNotification());
        account.setLearningMailNotification(modNotificationVO.isLearningMailNotification());
        account.setLearningWebNotification(modNotificationVO.isLearningWebNotification());

        return account;
    }

    /**
     * GET 단건 조회 For 프로필 화면용
     */
    @Override
    public Account getAccountForProfile(Long accountId) {
        return accountRepository.findAccountForProfile(accountId);
    }
}

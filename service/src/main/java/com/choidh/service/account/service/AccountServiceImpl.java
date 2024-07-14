package com.choidh.service.account.service;


import com.choidh.service.account.entity.Account;
import com.choidh.service.account.repository.AccountRepository;
import com.choidh.service.account.vo.*;
import com.choidh.service.cart.entity.Cart;
import com.choidh.service.cart.service.CartService;
import com.choidh.service.joinTables.entity.AccountTagJoinTable;
import com.choidh.service.learning.entity.Learning;
import com.choidh.service.mail.service.EmailService;
import com.choidh.service.mail.vo.EmailMessageVO;
import com.choidh.service.purchaseHistory.entity.PurchaseHistory;
import com.choidh.service.question.entity.Question;
import com.choidh.service.security.service.SecurityService;
import com.choidh.service.tag.entity.Tag;
import lombok.extern.slf4j.Slf4j;
import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.persistence.EntityManager;
import java.util.List;
import java.util.stream.Collectors;

@Slf4j
@Service
@Transactional(readOnly = true)
public class AccountServiceImpl implements AccountService {
    @Autowired private PasswordEncoder passwordEncoder;
    @Autowired private AccountRepository accountRepository;
    @Autowired private EmailService emailService;
    @Autowired private CartService cartService;
    @Autowired private SecurityService securityService;

    /**
     * Account 단건 조회 By Id
     */
    @Override
    public Account getAccountById(Long accountId) {
        return accountRepository.findByAccountId(accountId);
    }

    /**
     * Account 단건 조회 By Email
     */
    @Override
    public Account getAccountByEmail(String email) {
        return accountRepository.findByEmail(email);
    }

    /**
     * Account 단건 조회 By Email And Email Checked Token
     */
    @Override
    public Account getAccountByEmailAndChecked(String email, boolean checked) {
        return accountRepository.findByEmailAndChecked(email, checked);
    }

    /**
     * Account 단건 조회 By Id With Tags
     */
    @Override
    public Account getAccountByIdWithTags(Long accountId) {
        return accountRepository.findByIdWithTags(accountId);
    }

    /**
     * Account 단건 조회. By Id With Learning In Cart
     */
    @Override
    @Transactional
    public Account getAccountByIdWithLearningInCart(Long accountId) {
        // Account 단건 조회. By Id With LearningCartJoinTable
        Account account = accountRepository.findAccountByIdWithLearningCart(accountId);

        if (account.getCart() == null) {
            Cart cart = cartService.regCart(accountId);
            account.setCart(cart);
        }

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
        if (!regAccountVO.matchPassword()) throw new BadCredentialsException("패스워드가 일치하지 않습니다.");

        Account account = accountRepository.save(Account.builder()
                .email(regAccountVO.getEmail())
                .nickname(regAccountVO.getNickname())
                .password(passwordEncoder.encode(regAccountVO.getPassword()))
                .checked(false)
                .accountType(AccountType.USER)
                .build());

        Cart cart = cartService.regCart(account.getId());
        account.setCart(cart);

        // 메일 전송
        account.createTokenForEmailForAuthentication();
        emailService.sendEmail(EmailMessageVO.getInstanceForAccount(account));

        return account;
    }

    /**
     * 인증용 이메일 전송
     */
    @Override
    public boolean sendEmailForAuthentication(String email) {
        Account account = accountRepository.findByEmailAndChecked(email, false);

        if (account != null && account.canCheckingEmailToken()) {
            // 인증용 이메일 전송
            account.createTokenForEmailForAuthentication();
            emailService.sendEmail(EmailMessageVO.getInstanceForAccount(account));
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
        securityService.login(account);

        return true;
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
    public ProfileVO getAccountForProfile(Long accountId) {
        Account account = accountRepository.findAccountForProfile(accountId);

        List<String> tagTitleList = account.getTags()
                .stream().map(AccountTagJoinTable::getTag)
                .map(Tag::getTitle)
                .collect(Collectors.toList());

        List<Learning> learningList = account.getPurchaseHistories().stream().map(PurchaseHistory::getLearning).collect(Collectors.toList());
        List<Learning> learningTop4 = learningList.stream().limit(4).collect(Collectors.toList());
        List<String> learningTitle = learningList.stream().map(Learning::getTitle).limit(3).collect(Collectors.toList());
        List<Question> questionList = account.getQuestions().stream().limit(4).collect(Collectors.toList());

        return ProfileVO.builder()
                .account(account)
                .tagTitleList(tagTitleList)
                .learningTitleList(learningTitle)
                .learningTop4List(learningTop4)
                .questionList(questionList)
                .build();
    }

    /**
     * Chk 해당 강의를 수강중인 학생인지 검증
     */
    @Override
    public void chkAccountHasLearning(Long accountId, Long learningId) {
        // 해당 유저가 구매이력에 해당 강의를 가지고 있는지 검증
        boolean checker = false;
        Account account = this.getAccountByIdWithPurchaseHistories(accountId);

        for (PurchaseHistory purchaseHistory : account.getPurchaseHistories()) {
            if (purchaseHistory.getLearning().getId().equals(learningId)) {
                checker = true;
                break;
            }
        }

//        if (!checker) {
        // throw new IllegalArgumentException();
//        }
    }
}

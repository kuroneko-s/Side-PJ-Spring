package com.choidh.service.notification.service;


import com.choidh.service.account.entity.Account;
import com.choidh.service.account.service.AccountService;
import com.choidh.service.learning.entity.Learning;
import com.choidh.service.menu.service.MenuTypeServiceImpl;
import com.choidh.service.notification.entity.Notification;
import com.choidh.service.notification.repository.NotificationRepository;
import com.choidh.service.notification.vo.NotificationType;
import com.choidh.service.notification.vo.RegNotificationVO;
import com.choidh.service.purchaseHistory.entity.PurchaseHistory;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

import static com.choidh.service.common.AppConstant.getLearningNotFoundErrorMessage;
import static com.choidh.service.common.AppConstant.getNotificationNotFoundErrorMessage;

@Service
@Transactional
@RequiredArgsConstructor(onConstructor = @__(@Autowired))
public class NotificationServiceImpl implements NotificationService{
    private final NotificationRepository notificationRepository;
    private final AccountService accountService;
    private final MenuTypeServiceImpl menuTypeServiceImpl;

    @Override
    public void delNotification(Long notificationId) {
        notificationRepository.updateById(notificationId);
    }

    /**
     * 알람 목록조회 by LearningId
     */
    @Override
    public List<Notification> getNotificationListByType(Long accountId) {
        Account account = accountService.getAccountByIdWithPurchaseHistories(accountId);

        List<Learning> learningList = account.getPurchaseHistories().stream().map(PurchaseHistory::getLearning).collect(Collectors.toList());
        List<NotificationType> typeList = new ArrayList<>();
        if (account.isSiteWebNotification()) {
            typeList.add(NotificationType.SITE);
            typeList.add(NotificationType.EVENT);
        }

        if (account.isLearningWebNotification()) {
            typeList.add(NotificationType.NOTICE);
        }

        // NOTICE 를 구독하느냐 마냐에 따라서 learningId를 무력화 할 수 있음.
        return notificationRepository.findListByTypeAndLearning(
                typeList,
                learningList.stream().map(Learning::getId).collect(Collectors.toList())
        );
    }

    /**
     * 알람 갯수 조회 By Account Id
     */
    @Override
    public int getNotificationCountByAccount(Long accountId) {
        return accountService.getAccountByIdWithPurchaseHistories(accountId).getPurchaseHistories().size();
    }

    /**
     * 알림 추가.
     */
    @Override
    @Transactional
    public Notification regNotification(RegNotificationVO regNotificationVO) {
        Notification notification = Notification.builder()
                .title(regNotificationVO.getTitle())
                .description(regNotificationVO.getDescription())
                .notificationType(regNotificationVO.getNotificationType())
                .used(true)
                .notice(null)
                .learning(null)
                .event(null)
                .build();

        if (regNotificationVO.getEvent() != null) notification.setEvent(regNotificationVO.getEvent());
        if (regNotificationVO.getLearning() != null) notification.setLearning(regNotificationVO.getLearning());
        if (regNotificationVO.getNotice() != null) notification.setNotice(regNotificationVO.getNotice());

        return notificationRepository.save(notification);
    }

    /**
     * 알림 상세 조회. By Notification Id
     */
    @Override
    public Notification getNotificationById(Long notificationId) {
        return notificationRepository.findByIdWithAll(notificationId);
    }
}

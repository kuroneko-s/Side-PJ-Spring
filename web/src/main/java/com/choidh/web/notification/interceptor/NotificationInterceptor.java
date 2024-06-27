package com.choidh.web.notification.interceptor;

import com.choidh.service.account.entity.Account;
import com.choidh.service.notification.repository.NotificationRepository;
import com.choidh.service.notification.service.NotificationService;
import com.choidh.service.security.AccountUser;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;
import org.springframework.web.servlet.HandlerInterceptor;
import org.springframework.web.servlet.ModelAndView;
import org.springframework.web.servlet.view.RedirectView;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

@Component
@RequiredArgsConstructor(onConstructor = @__(@Autowired))
public class NotificationInterceptor implements HandlerInterceptor {
    private final NotificationService notificationService;
    private final NotificationRepository notificationRepository;

    /**
     * 알림 활성화 유무 확인
     */
    @Override
    public void postHandle(HttpServletRequest request, HttpServletResponse response, Object handler, ModelAndView modelAndView) throws Exception {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();

        if (
                modelAndView != null
                && !isRedirectView(modelAndView)
                && authentication != null
                && authentication.getPrincipal() instanceof AccountUser
        ) {
            Account account = ((AccountUser) authentication.getPrincipal()).getAccount();
            int accountPurchaseHistoriesCount = notificationService.getNotificationCountByAccount(account.getId());

            // TODO : 알림같은거 읽었으면 어느정도 마킹을 해주는 컬럼이 필요할 수도 있음. 근데 일단 보류. 급한건 아니니깐...
            modelAndView.addObject("hasNotification", accountPurchaseHistoriesCount > 0);
        }
    }

    private boolean isRedirectView(ModelAndView modelAndView) {
        return modelAndView.getViewName().startsWith("redirect:") || modelAndView.getView() instanceof RedirectView;
    }
}

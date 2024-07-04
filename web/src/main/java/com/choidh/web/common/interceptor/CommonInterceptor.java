package com.choidh.web.common.interceptor;

import com.choidh.service.security.vo.AccountUser;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;
import org.springframework.web.servlet.HandlerInterceptor;
import org.springframework.web.servlet.ModelAndView;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

@Component
@RequiredArgsConstructor(onConstructor = @__(@Autowired))
public class CommonInterceptor implements HandlerInterceptor {

    @Override
    public void postHandle(HttpServletRequest request, HttpServletResponse response, Object handler, ModelAndView modelAndView) throws Exception {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();

        if (authentication != null && authentication.isAuthenticated() && modelAndView != null) {
            Object principal = authentication.getPrincipal();
            if (principal instanceof AccountUser) {
                AccountUser account = (AccountUser) authentication.getPrincipal();
                modelAndView.addObject("account", account.getAccount());
            }
        }
    }
}

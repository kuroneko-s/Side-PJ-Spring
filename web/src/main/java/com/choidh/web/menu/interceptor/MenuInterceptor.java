package com.choidh.web.menu.interceptor;

import com.choidh.service.account.entity.Account;
import com.choidh.service.account.vo.AccountType;
import com.choidh.service.menu.entity.Menu;
import com.choidh.service.joinTables.entity.MenuTypeJoinTable;
import com.choidh.service.joinTables.service.MenuTypeService;
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
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Component
@RequiredArgsConstructor(onConstructor = @__(@Autowired))
public class MenuInterceptor implements HandlerInterceptor {
    private final MenuTypeService menuTypeService;

    @Override
    public void postHandle(HttpServletRequest request, HttpServletResponse response, Object handler, ModelAndView modelAndView) throws Exception {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();

        if (modelAndView != null && modelAndView.getViewName() != null) {
            List<Menu> menuList;

            if (authentication != null && authentication.getPrincipal() instanceof AccountUser) {
                Account account = ((AccountUser) authentication.getPrincipal()).getAccount();

                List<MenuTypeJoinTable> menuTypeList = menuTypeService.getListByAccountType(account.getAccountType());
                menuList = menuTypeList.stream().map(MenuTypeJoinTable::getMenu).collect(Collectors.toList());
            } else {
                List<MenuTypeJoinTable> menuTypeList = menuTypeService.getListByAccountType(AccountType.USER);
                menuList = menuTypeList.stream().map(MenuTypeJoinTable::getMenu).collect(Collectors.toList());
            }

            Map<String, List<Menu>> subMenuMap = new HashMap<>();

            List<Menu> mainMenuList = menuList.stream().filter(menu -> menu.getLevel() == 0).collect(Collectors.toList());
            for (Menu menu : mainMenuList) {
                subMenuMap.put(menu.getUrl(), new ArrayList<>());
            }

            List<Menu> subMenuList = menuList.stream().filter(menu -> menu.getLevel() != 0).collect(Collectors.toList());
            for (Menu menu : subMenuList) {
                List<Menu> menus = subMenuMap.get(menu.getParentUrl());
                menus.add(menu);
            }

            modelAndView.addObject("mainMenuList", mainMenuList);
            modelAndView.addObject("subMenuMap", subMenuMap);
        }
    }
}

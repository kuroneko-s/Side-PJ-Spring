package com.choidh.service.menu.service;

import com.choidh.service.account.vo.AccountType;
import com.choidh.service.menu.entity.Menu;
import com.choidh.service.menu.vo.RegMenuVO;

import java.util.List;

public interface MenuService {
    /**
     * 메뉴 추가
     */
    Menu regMenu(RegMenuVO regMenuVO);

    /**
     * 메뉴 목록 조회
     */
    List<Menu> getMenuList();

    /**
     * 메뉴 단건 조회 By Menu Id
     */
    Menu getMenuById(Long menuId);
}

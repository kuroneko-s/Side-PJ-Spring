package com.choidh.service.menu.service;

import com.choidh.service.account.vo.AccountType;
import com.choidh.service.menu.entity.MenuTypeJoinTable;
import com.choidh.service.menu.vo.RegMenuVO;

import java.util.List;

public interface MenuTypeService {
    /**
     * MenuTypeService 등록
     */
    List<MenuTypeJoinTable> regMenuTypeJoinTable(RegMenuVO regMenuVO);

    /**
     * MenuTypeService 목록 조회
     */
    List<MenuTypeJoinTable> getMenuTypeAll();

    /**
     * MenuTypeService 목록 조회 By AccountType
     */
    List<MenuTypeJoinTable> getListByAccountType(AccountType accountType);
}

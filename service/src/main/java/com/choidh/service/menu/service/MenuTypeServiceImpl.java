package com.choidh.service.menu.service;

import com.choidh.service.account.vo.AccountType;
import com.choidh.service.menu.entity.MenuTypeJoinTable;
import com.choidh.service.menu.repository.MenuTypeRepository;
import com.choidh.service.menu.entity.Menu;
import com.choidh.service.menu.vo.RegMenuVO;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;

@Service
public class MenuTypeServiceImpl implements MenuTypeService {
    @Autowired private MenuTypeRepository menuTypeRepository;
    @Autowired private MenuService menuService;

    /**
     * MenuTypeService 등록
     */
    @Override
    public List<MenuTypeJoinTable> regMenuTypeJoinTable(RegMenuVO regMenuVO) {
        Menu menu = menuService.regMenu(regMenuVO);
        List<MenuTypeJoinTable> resultList = new ArrayList<>();

        for (AccountType accountType : regMenuVO.getAccountTypeList()) {
            MenuTypeJoinTable menuTypeJoinTable = menuTypeRepository.save(
                    MenuTypeJoinTable.builder()
                            .accountType(accountType)
                            .menu(menu)
                            .used(true)
                            .build());
            resultList.add(menuTypeJoinTable);
        }

        return resultList;
    }

    /**
     * MenuTypeService 목록 조회 By AccountType
     */
    @Override
    public List<MenuTypeJoinTable> getListByAccountType(AccountType accountType) {
        return menuTypeRepository.findListByAccountType(accountType);
    }
}

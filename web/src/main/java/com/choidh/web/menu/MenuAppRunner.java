package com.choidh.web.menu;

import com.choidh.service.account.vo.AccountType;
import com.choidh.service.menu.entity.MenuTypeJoinTable;
import com.choidh.service.menu.service.MenuTypeService;
import com.choidh.service.menu.vo.RegMenuVO;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.ApplicationArguments;
import org.springframework.boot.ApplicationRunner;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
public class MenuAppRunner implements ApplicationRunner {
    @Autowired
    private MenuTypeService menuTypeService;

    @Override
    public void run(ApplicationArguments args) throws Exception {
        List<MenuTypeJoinTable> menuTypeList = menuTypeService.getMenuTypeAll();

        // /all(모든강의) -> /learning/all
        // /web/all(웹 개발) -> /learning/web
        // /algorithm/all(알고리즘) -> /learning/algorithm

        if (menuTypeList.isEmpty()) {
            RegMenuVO regMenuVO = RegMenuVO.builder()
                    .url("/learning")
                    .name("강의")
                    .level(0)
                    .menuOrder(0)
                    .parentUrl("ROOT")
                    .accountTypeList(List.of(
                            AccountType.USER,
                            AccountType.ADMIN,
                            AccountType.PROFESSIONAL)
                    )
                    .build();
            menuTypeService.regMenuTypeJoinTable(regMenuVO);

            regMenuVO = RegMenuVO.builder()
                    .url("/learning/all")
                    .name("모든 강의")
                    .level(1)
                    .menuOrder(1)
                    .parentUrl("/learning")
                    .accountTypeList(List.of(
                            AccountType.USER,
                            AccountType.ADMIN,
                            AccountType.PROFESSIONAL)
                    )
                    .build();
            menuTypeService.regMenuTypeJoinTable(regMenuVO);

            regMenuVO = RegMenuVO.builder()
                    .url("/learning/web")
                    .name("웹 개발")
                    .level(1)
                    .menuOrder(2)
                    .parentUrl("/learning")
                    .accountTypeList(List.of(
                            AccountType.USER,
                            AccountType.ADMIN,
                            AccountType.PROFESSIONAL)
                    )
                    .build();
            menuTypeService.regMenuTypeJoinTable(regMenuVO);

            regMenuVO = RegMenuVO.builder()
                    .url("/learning/all")
                    .name("알고리즘")
                    .level(1)
                    .menuOrder(3)
                    .parentUrl("/learning")
                    .accountTypeList(List.of(
                            AccountType.USER,
                            AccountType.ADMIN,
                            AccountType.PROFESSIONAL)
                    )
                    .build();
            menuTypeService.regMenuTypeJoinTable(regMenuVO);



            regMenuVO = RegMenuVO.builder()
                    .url("/community")
                    .name("커뮤니티")
                    .level(0)
                    .menuOrder(0)
                    .parentUrl("ROOT")
                    .accountTypeList(List.of(
                            AccountType.USER,
                            AccountType.ADMIN,
                            AccountType.PROFESSIONAL)
                    )
                    .build();
            menuTypeService.regMenuTypeJoinTable(regMenuVO);

            regMenuVO = RegMenuVO.builder()
                    .url("/community/1")
                    .name("커뮤니티1")
                    .level(1)
                    .menuOrder(1)
                    .parentUrl("/community")
                    .accountTypeList(List.of(
                            AccountType.USER,
                            AccountType.ADMIN,
                            AccountType.PROFESSIONAL)
                    )
                    .build();
            menuTypeService.regMenuTypeJoinTable(regMenuVO);

            regMenuVO = RegMenuVO.builder()
                    .url("/community/2")
                    .name("커뮤니티2")
                    .level(1)
                    .menuOrder(2)
                    .parentUrl("/community")
                    .accountTypeList(List.of(
                            AccountType.USER,
                            AccountType.ADMIN,
                            AccountType.PROFESSIONAL)
                    )
                    .build();
            menuTypeService.regMenuTypeJoinTable(regMenuVO);

            regMenuVO = RegMenuVO.builder()
                    .url("/community/3")
                    .name("커뮤니티3")
                    .level(1)
                    .menuOrder(3)
                    .parentUrl("/community")
                    .accountTypeList(List.of(
                            AccountType.USER,
                            AccountType.ADMIN,
                            AccountType.PROFESSIONAL)
                    )
                    .build();
            menuTypeService.regMenuTypeJoinTable(regMenuVO);
        }
    }
}

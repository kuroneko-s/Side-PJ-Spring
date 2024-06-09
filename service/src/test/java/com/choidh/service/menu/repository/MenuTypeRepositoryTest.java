package com.choidh.service.menu.repository;

import com.choidh.service.AbstractRepositoryTestConfig;
import com.choidh.service.account.vo.AccountType;
import com.choidh.service.menu.entity.Menu;
import com.choidh.service.menu.entity.MenuTypeJoinTable;
import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;

@Slf4j
class MenuTypeRepositoryTest extends AbstractRepositoryTestConfig {
    @Autowired
    private MenuTypeRepository menuTypeRepository;
    @Autowired
    private MenuRepository menuRepository;

    private MenuTypeJoinTable create(Menu menu, AccountType accountType) {
        return menuTypeRepository.save(MenuTypeJoinTable.builder()
                .menu(menu)
                .accountType(accountType)
                .used(true)
                .build());
    }

    @Test
    public void save() throws Exception {
        Menu save = menuRepository.save(Menu.builder()
                .url("/home")
                .level(1)
                .build());
        MenuTypeJoinTable menuTypeJoinTable = create(save, AccountType.ADMIN);

        assertNotNull(menuTypeJoinTable);
    }

    @Test
    @DisplayName("MenuTypeService 목록 조회 By AccountType")
    public void findListByAccountType() throws Exception {
        int count = 0;

        for (int i = 0; i < 30; i++) {
            Menu save = menuRepository.save(Menu.builder()
                    .url("/home" + i)
                    .level(i)
                    .build());

            AccountType accountType;
            if (i % 2 == 0) {
                accountType = AccountType.ADMIN;
                count++;
            } else {
                accountType = AccountType.USER;
            }

            create(save, accountType);
        }

        List<MenuTypeJoinTable> resultList = menuTypeRepository.findListByAccountType(AccountType.USER);
        assertEquals(resultList.size(), 30 - count);
    }
}
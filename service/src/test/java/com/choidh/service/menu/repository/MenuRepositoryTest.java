package com.choidh.service.menu.repository;

import com.choidh.service.AbstractRepositoryTestConfig;
import com.choidh.service.menu.entity.Menu;
import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;

import static org.junit.jupiter.api.Assertions.*;

@Slf4j
class MenuRepositoryTest extends AbstractRepositoryTestConfig {
    @Autowired
    private MenuRepository menuRepository;

    private Menu createMenu(String url, Integer level) {
        Menu menu = Menu.builder()
                .url(url)
                .level(level)
                .build();
        return menuRepository.save(menu);
    }

    @Test
    @DisplayName("메뉴 생성")
    public void create() throws Exception {
        Menu menu = createMenu("/home", 1);

        assertNotNull(menu);
    }
}
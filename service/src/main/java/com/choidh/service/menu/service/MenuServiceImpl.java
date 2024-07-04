package com.choidh.service.menu.service;

import com.choidh.service.menu.entity.Menu;
import com.choidh.service.menu.repository.MenuRepository;
import com.choidh.service.menu.vo.RegMenuVO;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

import static com.choidh.service.common.vo.AppConstant.getMenuNotFoundErrorMessage;

@Service
public class MenuServiceImpl implements MenuService {
    @Autowired
    private MenuRepository menuRepository;

    /**
     * 메뉴 추가
     */
    @Override
    public Menu regMenu(RegMenuVO regMenuVO) {
        return menuRepository.save(
                Menu.builder()
                        .url(regMenuVO.getUrl())
                        .level(regMenuVO.getLevel())
                        .name(regMenuVO.getName())
                        .parentUrl(regMenuVO.getParentUrl())
                        .menuOrder(regMenuVO.getMenuOrder())
                        .build()
        );
    }

    /**
     * 메뉴 목록 조회
     */
    @Override
    public List<Menu> getMenuList() {
        return menuRepository.findAll();
    }

    /**
     * 메뉴 단건 조회 By Menu Id
     */
    @Override
    public Menu getMenuById(Long menuId) {
        return menuRepository.findById(menuId)
                .orElseThrow(() -> new IllegalArgumentException(getMenuNotFoundErrorMessage(menuId)));
    }
}

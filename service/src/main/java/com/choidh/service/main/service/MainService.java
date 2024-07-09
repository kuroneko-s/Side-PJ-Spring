package com.choidh.service.main.service;

import com.choidh.service.account.entity.Account;
import com.choidh.service.main.vo.HomeVO;

import javax.annotation.Nullable;

public interface MainService {
    /**
     * 홈 서비스
     */
    HomeVO getHomeVO(@Nullable Account account);
}

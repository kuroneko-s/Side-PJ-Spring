package com.choidh.service.joinTables.service;

import com.choidh.service.joinTables.entity.AccountTagJoinTable;
import com.choidh.service.tag.vo.RegTagVO;

import java.util.Set;

public interface AccountTagService {
    /**
     * 태그 목록조회 By AccountId
     */
    Set<AccountTagJoinTable> getTagListByAccountId(Long accountId);

    /**
     * 태그 추가
     */
    AccountTagJoinTable regTag(RegTagVO regTagVO, Long accountId);

    /**
     * 태그 삭제
     */
    boolean delTag(Long tagId, Long accountId);
}

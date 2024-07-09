package com.choidh.service.joinTables.service;

import com.choidh.service.account.entity.Account;
import com.choidh.service.account.service.AccountService;
import com.choidh.service.joinTables.entity.AccountTagJoinTable;
import com.choidh.service.joinTables.repository.AccountTagRepository;
import com.choidh.service.tag.entity.Tag;
import com.choidh.service.tag.service.TagService;
import com.choidh.service.tag.vo.RegTagVO;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Set;

@Slf4j
@Service
@Transactional(readOnly = true)
@RequiredArgsConstructor
public class AccountTagServiceImpl implements AccountTagService {
    private final AccountTagRepository accountTagRepository;
    private final AccountService accountService;
    private final TagService tagService;

    @Override
    public Set<AccountTagJoinTable> getTagListByAccountId(Long accountId) {
        return accountTagRepository.findTagListByAccountId(accountId);
    }

    /**
     * 유저 목록조회 By Tag ids
     */
    @Override
    public Set<AccountTagJoinTable> getAccountListByTagIds(Set<Tag> tags) {
        return accountTagRepository.findAccountListByTags(tags);
    }

    /**
     * 태그 추가
     */
    @Override
    @Transactional
    public AccountTagJoinTable regTag(RegTagVO regTagVO, Long accountId) {
        Account account = accountService.getAccountById(accountId);

        Tag tag = tagService.regTag(regTagVO);

        return accountTagRepository.save(AccountTagJoinTable.builder()
                .account(account)
                .tag(tag)
                .build());
    }

    /**
     * 태그 삭제
     */
    @Override
    @Transactional
    public boolean delTag(String tagTitle, Long accountId) {
        try {
            Tag tag = tagService.getTagByTitle(tagTitle);
            accountTagRepository.deleteByAccountIdAndTagId(accountId, tag.getId());

            return true;
        } catch (Exception e) {
            log.error(e.getMessage(), e);

            return false;
        }
    }
}

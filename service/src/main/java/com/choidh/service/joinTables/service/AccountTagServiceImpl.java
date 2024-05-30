package com.choidh.service.joinTables.service;

import com.choidh.service.account.entity.Account;
import com.choidh.service.account.service.AccountService;
import com.choidh.service.joinTables.entity.AccountTagJoinTable;
import com.choidh.service.joinTables.repository.AccountTagRepository;
import com.choidh.service.tag.entity.Tag;
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

    @Override
    public Set<AccountTagJoinTable> getTagListByAccountId(Long accountId) {
        return accountTagRepository.findTagListByAccountId(accountId);
    }

    /**
     * 태그 추가
     */
    @Override
    @Transactional
    public AccountTagJoinTable regTag(RegTagVO regTagVO, Long accountId) {
        Account account = accountService.getAccountById(accountId);
        Tag tag = Tag.builder()
                .title(regTagVO.getTitle())
                .build();

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
    public boolean delTag(Long tagId, Long accountId) {
        try {
            accountTagRepository.deleteByAccountIdAndTagId(accountId, tagId);

            return true;
        } catch (Exception e) {
            log.error(e.getMessage(), e);

            return false;
        }
    }
}

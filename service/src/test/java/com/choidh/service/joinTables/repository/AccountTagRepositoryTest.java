package com.choidh.service.joinTables.repository;

import com.choidh.service.AbstractRepositoryTestConfig;
import com.choidh.service.account.entity.Account;
import com.choidh.service.joinTables.entity.AccountTagJoinTable;
import com.choidh.service.tag.entity.Tag;
import com.choidh.service.tag.repository.TagRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

@Slf4j
//@Rollback(value = false)
@RequiredArgsConstructor(onConstructor = @__(@Autowired))
class AccountTagRepositoryTest extends AbstractRepositoryTestConfig {
    private final AccountTagRepository accountTagRepository;
    private final TagRepository tagRepository;

    private AccountTagJoinTable createAccountTagJoinTable(Account account, String tagTitle) {
        Tag tag = tagRepository.save(Tag.builder()
                .title(tagTitle)
                .build());

        return accountTagRepository.save(AccountTagJoinTable.builder()
                .account(account)
                .tag(tag)
                .build());
    }

    @Test
    @DisplayName("AccountTagJoinTable 목록 조회 By Account Id")
    public void findTagListByAccountId() throws Exception {
        Account account = createAccount();
        List<AccountTagJoinTable> targetList = new ArrayList<>();

        for (int i = 0; i < 5; i++) {
            targetList.add(createAccountTagJoinTable(account, "tag " + i));
        }

        theLine();

        Set<AccountTagJoinTable> accountTagJoinTables = accountTagRepository.findTagListByAccountId(account.getId());
        accountTagJoinTables.forEach(accountTagJoinTable -> assertTrue(targetList.contains(accountTagJoinTable)));
        assertEquals(accountTagJoinTables.size(), 5);
    }

    @Test
    @DisplayName("AccountTagJoinTable 삭제. By Account Id And Tag Id")
    public void deleteByAccountIdAndTagId() throws Exception {
        Account account = createAccount();
        List<AccountTagJoinTable> targetList = new ArrayList<>();

        for (int i = 0; i < 5; i++) {
            targetList.add(createAccountTagJoinTable(account, "tag " + i));
        }

        theLine();

        AccountTagJoinTable deleteTarget = targetList.get(3);
        int deleteResult = accountTagRepository.deleteByAccountIdAndTagId(account.getId(), deleteTarget.getId());

        assertEquals(deleteResult, 1);

        Set<AccountTagJoinTable> accountTagJoinTables = accountTagRepository.findTagListByAccountId(account.getId());
        List<AccountTagJoinTable> collect = targetList.stream().filter(accountTagJoinTable -> !accountTagJoinTables.contains(accountTagJoinTable)).collect(Collectors.toList());

        assertEquals(collect.size(), 1);
        assertEquals(collect.get(0), deleteTarget);
    }
}
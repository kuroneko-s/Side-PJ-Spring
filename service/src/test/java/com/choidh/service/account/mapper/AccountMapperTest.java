package com.choidh.service.account.mapper;

import com.choidh.service.account.entity.Account;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

@Slf4j
@ActiveProfiles("local")
@SpringBootTest
@Transactional
@RequiredArgsConstructor(onConstructor = @__(@Autowired))
class AccountMapperTest {
    private final AccountMapper accountMapper;

    @Test
    public void getAccountAll() throws Exception {
        List<Account> accountList = accountMapper.getAccountAll();

        assertTrue(accountList.isEmpty());
    }

}
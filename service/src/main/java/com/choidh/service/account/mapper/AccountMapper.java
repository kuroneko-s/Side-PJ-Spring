package com.choidh.service.account.mapper;

import com.choidh.service.account.entity.Account;
import org.apache.ibatis.annotations.Mapper;

import java.util.List;

@Mapper
public interface AccountMapper {
    List<Account> getAccountAll();
}

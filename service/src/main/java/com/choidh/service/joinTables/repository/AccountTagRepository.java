package com.choidh.service.joinTables.repository;

import com.choidh.service.joinTables.entity.AccountTagJoinTable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.Set;

public interface AccountTagRepository extends JpaRepository<AccountTagJoinTable, Long> {
    @Query(value = "select atjt from AccountTagJoinTable atjt where atjt.account.id = :accountId")
    Set<AccountTagJoinTable> findTagListByAccountId(Long accountId);

    @Query(value = "delete from AccountTagJoinTable atjt where atjt.id = :tagId and atjt.account = :accountId")
    void deleteByAccountIdAndTagId(Long accountId, Long tagId);
}

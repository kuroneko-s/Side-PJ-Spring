package com.choidh.service.joinTables.repository;

import com.choidh.service.joinTables.entity.AccountTagJoinTable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;

import java.util.Set;

public interface AccountTagRepository extends JpaRepository<AccountTagJoinTable, Long> {
    /**
     * AccountTagJoinTable 목록 조회 By Account Id
     */
    @Query(value = "select atjt " +
            "from AccountTagJoinTable atjt " +
            "where atjt.account.id = :accountId")
    Set<AccountTagJoinTable> findTagListByAccountId(Long accountId);

    /**
     * AccountTagJoinTable 삭제. By Account Id And Tag Id
     */
    @Query(value = "delete from AccountTagJoinTable atjt " +
            "where atjt.tag.id = :tagId " +
            "and atjt.account.id = :accountId")
    @Modifying
    int deleteByAccountIdAndTagId(Long accountId, Long tagId);
}

package com.choidh.service.joinTables.repository;

import com.choidh.service.account.vo.web.AccountType;
import com.choidh.service.joinTables.entity.MenuTypeJoinTable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.List;

public interface MenuTypeRepository extends JpaRepository<MenuTypeJoinTable, Long> {
    /**
     * MenuTypeService 목록 조회 By AccountType
     */
    @Query(value = "select mt " +
            "from MenuTypeJoinTable mt " +
            "join fetch mt.menu m " +
            "where mt.used = true " +
            "and mt.accountType = :accountType " +
            "order by m.level ASC, m.menuOrder ASC ")
    List<MenuTypeJoinTable> findListByAccountType(AccountType accountType);
}

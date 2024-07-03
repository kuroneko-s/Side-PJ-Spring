package com.choidh.service.notice.repository;

import com.choidh.service.notice.entity.Notice;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.querydsl.QuerydslPredicateExecutor;

public interface NoticeRepository extends JpaRepository<Notice, Long>, QuerydslPredicateExecutor<Notice>, NoticeRepositoryExtension {
    @Query(value = "delete Notice n " +
            "where n.id = :noticeId")
    @Modifying
    void delNotice(Long noticeId);
}

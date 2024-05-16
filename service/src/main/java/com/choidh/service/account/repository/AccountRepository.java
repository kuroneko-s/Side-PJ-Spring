package com.choidh.service.account.repository;


import com.choidh.service.account.entity.Account;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.querydsl.QuerydslPredicateExecutor;

public interface AccountRepository extends JpaRepository<Account, Long>, QuerydslPredicateExecutor<Account> {
    /**
     * 이메일 중복여부 확인
     */
    boolean existsByEmail(String email);

    /**
     * 닉네임 중복여부 확인
     */
    boolean existsByNickname(String nickname);

    /**
     * 유저 단건 조회 By Email and Email Token Checked
     */
    Account findByEmailAndTokenChecked(String email, boolean check);

    /**
     * 유저 단건 조회 By 닉네임 and Email Token Checked
     */
    Account findByNicknameAndTokenChecked(String nickname, boolean check);

    // @EntityGraph EAGER 사용하는 방식에서 join fetch 방식으로 변경. TODO: 동작에 이상있는지 확인 필요
    /**
     * 유저 단건 조회 with learning
     */
    @Query("select a from Account a left join fetch a.learnings")
    Account findAccountWithLearnings(Long id);

    /**
     * 유저 단건 조회 with question
     */
    @Query("select a from Account a left join fetch a.questions")
    Account findAccountWithQuestion(Long id);

    /**
     * 유저 단건 조회 with tag
     */
    @Query("select a from Account a left join fetch a.tags")
    Account findAccountWithTags(Long id);

    /**
     * 유저 단건 조회 with learning, question, listenLearning
     */
    @Query("select a from Account a " +
            "left join fetch a.learnings " +
            "left join fetch a.questions " +
            "left join fetch a.listenLearning")
    Account findAccountWithAll(Long id);
}

package com.choidh.service.account.repository;


import com.choidh.service.account.entity.Account;
import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.querydsl.QuerydslPredicateExecutor;

public interface AccountRepository extends JpaRepository<Account, Long>, QuerydslPredicateExecutor<Account> {
    @Query(value = "select a " +
            "from Account a " +
            "join fetch a.cart ac " +
            "join fetch ac.learningCartJoinTables lcjt " +
            "join fetch lcjt.learning " +
            "where a.id = :accountId")
    Account findAccountByIdWithLearning(Long accountId);

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
    Account findByEmailAndChecked(String email, boolean check);

    /**
     * 유저 단건 조회 By 닉네임 and Email Token Checked
     */
    Account findByNicknameAndChecked(String nickname, boolean check);

    /**
     * 유저 단건 조회 with learning
     */
    @Query("select a " +
            "from Account a " +
            "join fetch a.purchaseHistories pur " +
            "join fetch pur.learning " +
            "where a.id = :id")
    Account findAccountWithLearnings(Long id);

    /**
     * 유저 단건 조회 with question
     */
    @Query("select a " +
            "from Account a " +
            "join fetch a.questions " +
            "where a.id = :id")
    Account findAccountWithQuestion(Long id);

    /**
     * 유저 단건 조회 with tag
     */
    @Query("select a " +
            "from Account a " +
            "join fetch a.tags " +
            "where a.id = :id")
    Account findAccountWithTags(Long id);

    /**
     * 유저 단건 조회 For 프로필 화면용
     */
    @Query(value = "select acc " +
            "from Account acc " +
            "where acc.id = :accountId")
    @EntityGraph(attributePaths = {"tags", "purchaseHistories", "questions"}, type = EntityGraph.EntityGraphType.LOAD)
    Account findAccountForProfile(Long accountId);
}

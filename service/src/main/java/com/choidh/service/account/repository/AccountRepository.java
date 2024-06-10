package com.choidh.service.account.repository;


import com.choidh.service.account.entity.Account;
import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.querydsl.QuerydslPredicateExecutor;

import java.util.Optional;

public interface AccountRepository extends JpaRepository<Account, Long>, QuerydslPredicateExecutor<Account> {
    @EntityGraph(attributePaths = {"cart", "professionalAccount"})
    Optional<Account> findById(Long id);

    /**
     * Account 단건 조회. By Id With LearningCartJoinTable
     */
    @Query(value = "select a " +
            "from Account a " +
            "left join fetch a.cart ac " +
            "left join fetch ac.learningCartJoinTables " +
            "left join fetch a.professionalAccount " +
            "where a.id = :accountId")
    Account findAccountByIdWithLearningCart(Long accountId);

    /**
     * Account 단건 조회 By Id With Tags
     */
    @Query(value = "select a " +
            "from Account a " +
            "left join fetch a.tags " +
            "where a.id = :accountId")
    Account findByIdWithTags(Long accountId);

    /**
     * 이메일 중복여부 확인
     */
    boolean existsByEmail(String email);

    /**
     * 닉네임 중복여부 확인
     */
    boolean existsByNickname(String nickname);

    /**
     * Account 단건 조회 By Email and Email Token Checked
     */
    @Query(value = "select a " +
            "from Account a " +
            "where a.checked = :checked " +
            "and a.email = :email")
    @EntityGraph(attributePaths = {"professionalAccount", "cart"}, type = EntityGraph.EntityGraphType.LOAD)
    Account findByEmailAndChecked(String email, boolean checked);

    /**
     * Account 단건 조회 By 닉네임 and Email Token Checked
     */
    @EntityGraph(attributePaths = {"professionalAccount", "cart"}, type = EntityGraph.EntityGraphType.LOAD)
    Account findByNicknameAndChecked(String nickname, boolean check);

    /**
     * Account 단건 조회 By Id with Learning
     */
    @Query("select a " +
            "from Account a " +
            "where a.id = :id")
    @EntityGraph(attributePaths = {"purchaseHistories.learning", "professionalAccount", "cart"}, type = EntityGraph.EntityGraphType.LOAD)
    Account findAccountWithLearnings(Long id);

    /**
     * Account 단건 조회 with Questions
     */
    @Query("select a " +
            "from Account a " +
            "where a.id = :id")
    @EntityGraph(attributePaths = {"professionalAccount", "cart", "questions"}, type = EntityGraph.EntityGraphType.LOAD)
    Account findAccountWithQuestion(Long id);

    /**
     * Account 단건 조회 with Tags
     */
    @Query("select a " +
            "from Account a " +
            "where a.id = :id")
    @EntityGraph(attributePaths = {"tags.tag", "cart", "questions", "professionalAccount"}, type = EntityGraph.EntityGraphType.LOAD)
    Account findAccountWithTags(Long id);

    /**
     * Account 단건 조회 For 프로필 화면용
     */
    @Query(value = "select a " +
            "from Account a " +
            "where a.id = :accountId")
    @EntityGraph(attributePaths = {"tags.tag", "cart", "purchaseHistories.learning", "questions", "reviews"}, type = EntityGraph.EntityGraphType.LOAD)
    Account findAccountForProfile(Long accountId);

    /**
     * Account 단건 조회 By Account Id With PurchaseHistories
     */
    @Query(value = "select a " +
            "from Account a " +
            "where a.id = :accountId")
    @EntityGraph(attributePaths = {"cart", "professionalAccount", "purchaseHistories"}, type = EntityGraph.EntityGraphType.LOAD)
    Account findAccountWithPurchaseHistories(Long accountId);
}

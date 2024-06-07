package com.choidh.service.cart.repository;

import com.choidh.service.cart.entity.Cart;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

public interface CartRepository extends JpaRepository<Cart, Long> {
    /**
     * 카트 조회. By Account Id With Learning
     */
    @Query(value = "select c " +
            "from Cart c " +
            "join fetch c.account " +
            "left join fetch c.learningCartJoinTables lcjt " +
            "left join fetch lcjt.learning " +
            "where c.account.id = :accountId")
    Cart findByAccountIdWithLearningCartJoinTables(Long accountId);
}

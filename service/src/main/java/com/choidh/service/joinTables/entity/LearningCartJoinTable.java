package com.choidh.service.joinTables.entity;

import com.choidh.service.cart.entity.Cart;
import com.choidh.service.common.entity.BaseEntity;
import com.choidh.service.learning.entity.Learning;
import lombok.AllArgsConstructor;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.NoArgsConstructor;

import javax.persistence.*;

@Entity
@EqualsAndHashCode(of = "id", callSuper = false)
@Getter
@NoArgsConstructor
@AllArgsConstructor
public class LearningCartJoinTable extends BaseEntity {
    @Id
    @GeneratedValue
    @Column(name = "learning_cart_id")
    private Long id;

    @ManyToOne
    @JoinColumn(name = "learning_id")
    private Learning learning;

    @ManyToOne
    @JoinColumn(name = "cart_id")
    private Cart cart;

    public void setCart(Cart cart) {
        this.cart = cart;

        if (!cart.getLearningCartJoinTables().contains(this)) {
            cart.getLearningCartJoinTables().add(this);
        }
    }

    public void setLearning(Learning learning) {
        this.learning = learning;

        if (!learning.getCarts().contains(this)) {
            learning.getCarts().add(this);
        }
    }
}

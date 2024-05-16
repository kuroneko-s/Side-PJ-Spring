package com.choidh.service.learning.entity;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import javax.persistence.Column;
import javax.persistence.Embeddable;
import java.io.Serializable;
import java.util.Objects;

/**
 *  Account and Learning 복합키.
 *  사용하는곳이 없는데 ?
 */

@Getter @Setter
@Embeddable
@NoArgsConstructor
@AllArgsConstructor
public class LearningCompositeKey implements Serializable {
    @Column(name = "learning_id")
    private Long learning;

    @Column(name = "account_id")
    private Long account;

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        LearningCompositeKey that = (LearningCompositeKey) o;
        return Objects.equals(learning, that.learning) &&
                Objects.equals(account, that.account);
    }

    @Override
    public int hashCode() {
        return Objects.hash(learning, account);
    }
}

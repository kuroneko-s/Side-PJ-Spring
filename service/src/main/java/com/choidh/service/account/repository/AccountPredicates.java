package com.choidh.service.account.repository;


import com.choidh.service.account.entity.QAccount;
import com.querydsl.core.types.Predicate;
import org.springframework.stereotype.Component;

import java.util.Set;

/**
 * predicate
 */
@Component
public class AccountPredicates {
    public Predicate findByTags(Set<Long> tags) {
        return QAccount.account.tags.any().id.in(tags);
    }
}

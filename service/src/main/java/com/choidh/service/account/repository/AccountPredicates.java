package com.choidh.service.account.repository;


import com.choidh.service.account.entity.QAccount;
import com.choidh.service.tag.entity.Tag;
import com.querydsl.core.types.Predicate;

import java.util.Set;

/**
 * Querydsl predicate
 */
public class AccountPredicates {
    public static Predicate findByTags(Set<Tag> tags) {
        return QAccount.account.tags.any().in(tags);
    }
}

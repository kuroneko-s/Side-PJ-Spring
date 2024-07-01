package com.choidh.service.event.repository;

import com.choidh.service.event.entity.Event;
import com.choidh.service.learning.entity.Learning;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.querydsl.QuerydslPredicateExecutor;

import java.util.List;

public interface EventRepository extends JpaRepository<Event, Long>, QuerydslPredicateExecutor<Learning>, EventRepositoryExtension {
    List<Event> findListByUsedIsTrue();
}

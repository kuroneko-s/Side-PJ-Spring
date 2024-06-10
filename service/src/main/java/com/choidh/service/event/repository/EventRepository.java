package com.choidh.service.event.repository;

import com.choidh.service.event.entity.Event;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface EventRepository extends JpaRepository<Event, Long> {
    List<Event> findListByUsedIsTrue();
}

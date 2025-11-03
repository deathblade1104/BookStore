package com.bookstore.repository;

import com.bookstore.domain.outbox.OutboxEvent;
import com.bookstore.domain.outbox.OutboxEventStatus;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface OutboxRepository extends JpaRepository<OutboxEvent, Long> {
    List<OutboxEvent> findByStatusOrderByCreatedAtAsc(OutboxEventStatus status);
}

package com.bookstore.services;

import com.bookstore.domain.outbox.OutboxEvent;
import com.bookstore.domain.outbox.OutboxEventStatus;
import com.bookstore.repository.OutboxRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Component
@RequiredArgsConstructor
@Slf4j
public class OutboxPublisher {

    private final OutboxRepository outboxRepository;
    private final KafkaTemplate<String, String> kafkaTemplate;

    @Scheduled(fixedDelay = 5000)
    @Transactional
    public void publishPending() {
        List<OutboxEvent> pending = outboxRepository.findByStatusOrderByCreatedAtAsc(OutboxEventStatus.PENDING);
        for (OutboxEvent e : pending) {
            try {
                kafkaTemplate.send(e.getEventType().name(), e.getAggregateId(), e.getPayload()).get();
                e.setStatus(OutboxEventStatus.PUBLISHED);
                outboxRepository.save(e);
                log.info("Published outbox id={} eventType={}", e.getId(), e.getEventType());
            } catch (Exception ex) {
                if(e.getAttemptCount() < 3) {
                    e.setAttemptCount(e.getAttemptCount() + 1);
                }
                else{
                    e.setStatus(OutboxEventStatus.FAILED);
                }
                outboxRepository.save(e);
                log.error("Failed to publish outbox id={}, attempt={}", e.getId(), e.getAttemptCount(), ex);
            }
        }
    }
}

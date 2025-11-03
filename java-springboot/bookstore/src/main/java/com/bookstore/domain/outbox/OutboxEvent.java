package com.bookstore.domain.outbox;

import jakarta.persistence.*;
import lombok.*;

import java.time.Instant;

import com.bookstore.constants.TableNames;

@Entity
@Table(name = TableNames.OUTBOX_EVENTS)
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class OutboxEvent {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false, name = "aggregate_type")
    private AggregateType aggregateType;

    @Column(nullable = false)
    private String aggregateId;

    @Column(nullable = false, columnDefinition = "TEXT")
    private String payload;

    @Column(nullable = false, name = "event_type")
    @Enumerated(EnumType.STRING)
    private OutboxEventType eventType;

    @Column(nullable = false, name = "created_at")
    private Instant createdAt;

    @Column(nullable = false)
    @Enumerated(EnumType.STRING)
    @Builder.Default
    private OutboxEventStatus status = OutboxEventStatus.PENDING;

    @Builder.Default
    @Column(nullable = false, name = "attempt_count")
    private int attemptCount = 0;

    @PrePersist
    protected void onPersist() {
      createdAt = Instant.now();
    }
}

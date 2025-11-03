package com.bookstore.domain.outbox;

public enum OutboxEventStatus {
  PENDING,
  PUBLISHED,
  FAILED

}

package com.bookstore.services;

import com.bookstore.domain.book.Author;
import com.bookstore.domain.book.Book;
import com.bookstore.domain.outbox.AggregateType;
import com.bookstore.domain.outbox.OutboxEvent;
import com.bookstore.domain.outbox.OutboxEventType;
import com.bookstore.dto.CreateBookRequest;
import com.bookstore.repository.AuthorRepository;
import com.bookstore.repository.BookRepository;
import com.bookstore.repository.OutboxRepository;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;

import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class BookService {

    private final BookRepository bookRepository;
    private final AuthorRepository authorRepository;
    private final OutboxRepository outboxRepository;
    private final ObjectMapper objectMapper;

    public List<Book> getAllBooks() {
        return bookRepository.findAll();
    }

    @Cacheable(value = "book", key = "#id")
    public Book getBookById(Long id) {
        return bookRepository.findById(id)
        .orElseThrow(() -> new IllegalArgumentException("Book not found"));
    }

    @Transactional
    public Book createBook(CreateBookRequest request) {
        Author author = authorRepository.findById(request.getAuthorId())
                .orElseThrow(() -> new IllegalArgumentException("Author not found"));

        Book book = Book.builder()
                .title(request.getTitle())
                .description(request.getDescription())
                .price(request.getPrice())
                .stock(request.getStock())
                .genre(request.getGenre())
                .author(author)
                .build();

        Book saved =  bookRepository.save(book);
        try {
            String payload = objectMapper.writeValueAsString(saved);
            OutboxEvent event = OutboxEvent.builder()
                        .aggregateType(AggregateType.BOOK)
                        .aggregateId(String.valueOf(saved.getId()))
                        .eventType(OutboxEventType.BOOK_CREATED)
                        .payload(payload)
                        .build();

            outboxRepository.save(event);
        } catch (JsonProcessingException e) {
            throw new RuntimeException("Failed to serialize book for outbox event", e);
        }

        return saved;
    }

    @Transactional
    @CacheEvict(value = "book", key = "#bookId")
    public Book updateStock(Long bookId, int delta) {
        Book book = bookRepository.findById(bookId)
                .orElseThrow(() -> new IllegalArgumentException("Book not found"));

        int newStock = book.getStock() + delta;
        if (newStock < 0) throw new IllegalStateException("Insufficient stock");

        book.setStock(newStock);
        return bookRepository.save(book);
    }
}

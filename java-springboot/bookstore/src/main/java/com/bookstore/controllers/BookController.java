package com.bookstore.controllers;

import com.bookstore.domain.book.Book;
import com.bookstore.dto.CreateBookRequest;
import com.bookstore.services.BookService;

import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/books")
@RequiredArgsConstructor
public class BookController {

    private final BookService bookService;
    @GetMapping
    public ResponseEntity<List<Book>> getAllBooks() {
        return ResponseEntity.ok(bookService.getAllBooks());
    }

    @PostMapping
    public ResponseEntity<Book> createBook(@RequestBody CreateBookRequest request) {
        return ResponseEntity.ok(bookService.createBook(request));
    }

    @PatchMapping("/{bookId}/stock")
    public ResponseEntity<Book> updateStock(
            @PathVariable Long bookId,
            @RequestParam int delta
    ) {
        return ResponseEntity.ok(bookService.updateStock(bookId, delta));
    }
}

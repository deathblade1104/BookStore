package com.bookstore.domain.book;

import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDateTime;
import java.util.List;

import com.bookstore.constants.TableNames;

@Entity
@Table(name = TableNames.AUTHORS)
@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class Author {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private String name;

    @Column(columnDefinition = "TEXT")
    private String bio;

    @OneToMany(mappedBy = "author", cascade = CascadeType.ALL)
    private List<Book> books;

    @Column(updatable = false, name = "created_at")
    private LocalDateTime createdAt;

    @PrePersist
    protected void onPersist() {
        createdAt = LocalDateTime.now();
    }
}

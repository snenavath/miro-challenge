package com.rest.notes.models;

import lombok.*;
import lombok.extern.jackson.Jacksonized;

import javax.persistence.*;
import java.util.Objects;

@Getter
@Setter
@Builder
@ToString
@Jacksonized
@NoArgsConstructor
@AllArgsConstructor
@Entity
@Table(name = "notes")
public class Note {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    Long id;
    String title;
    String description;
    String author;
    boolean isShared;
    @ManyToOne(cascade = CascadeType.ALL)
    private User user;

    public Note(String title, String description, String author, boolean isShared, User user) {
        this.title = title;
        this.description = description;
        this.author = author;
        this.isShared = isShared;
        this.user = user;
    }
}

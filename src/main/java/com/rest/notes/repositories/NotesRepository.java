package com.rest.notes.repositories;

import com.rest.notes.models.Note;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.List;

public interface NotesRepository extends JpaRepository<Note, Long> {
    @Query("SELECT n from Note n where n.user.email=?1")
    List<Note> findByUser(String email);

    @Query("SELECT p FROM Note p WHERE CONCAT(p.title, p.description, p.author) LIKE %?1%")
    public List<Note> search(String keyword);
}

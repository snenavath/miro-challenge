package com.rest.notes;

import com.rest.notes.models.Note;
import com.rest.notes.models.User;
import com.rest.notes.repositories.NotesRepository;
import com.rest.notes.repositories.UserRepository;
import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.jdbc.EmbeddedDatabaseConnection;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.test.context.junit.jupiter.SpringExtension;

import java.util.List;
import java.util.NoSuchElementException;

import static org.junit.jupiter.api.Assertions.assertThrows;

@DataJpaTest
@ExtendWith(SpringExtension.class)
@AutoConfigureTestDatabase(connection = EmbeddedDatabaseConnection.H2)
public class NotesRepositoryUnitTest {

    @Autowired
    NotesRepository notesRepository;

    @Autowired
    UserRepository userRepository;

    @BeforeEach
    public void setUp() {
        User user = new User("test@test.com", "test1234");
        userRepository.save(user);
        notesRepository.save(new Note("todo1", "wash car", "test", false, user));
        notesRepository.save(new Note("todo2", "rent house", "test", false, user));
    }

    @AfterEach
    public void destroy() {
        notesRepository.deleteAll();
    }

    @Test
    public void testFindNotesByUser() {
        List<Note> notes = notesRepository.findByUser("test@test.com");
        Assertions.assertThat(notes.size()).isEqualTo(2);
        Assertions.assertThat(notes.get(0).getTitle()).isEqualTo("todo1");
        Assertions.assertThat(notes.get(1).getTitle()).isEqualTo("todo2");
    }

    @Test
    public void testSearchNotesByKeyword() {
        List<Note> notes = notesRepository.search("house");

        Assertions.assertThat(notes.size()).isEqualTo(1);
        Assertions.assertThat(notes.get(0).getTitle()).isEqualTo("todo2");
        Assertions.assertThat(notes.get(0).getDescription()).isEqualTo("rent house");
    }

}

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
import java.util.Optional;

@DataJpaTest
@ExtendWith(SpringExtension.class)
@AutoConfigureTestDatabase(connection = EmbeddedDatabaseConnection.H2)
public class UserRepositoryUnitTest {

    @Autowired
    UserRepository userRepository;

    @BeforeEach
    public void setUp() {
        User user = new User("test@test.com", "test1234");
        userRepository.save(user);
      }

    @AfterEach
    public void destroy() {
        userRepository.deleteAll();
    }

    @Test
    public void testFindUserByEmail() {
        Optional<User> user = userRepository.findByEmail("test@test.com");
        Assertions.assertThat(user.isPresent()).isEqualTo(true);
        Assertions.assertThat(user.get().getEmail()).isEqualTo("test@test.com");
    }

}

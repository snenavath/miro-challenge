package com.rest.notes;

import com.rest.notes.models.Note;
import com.rest.notes.models.User;
import com.rest.notes.repositories.NotesRepository;
import com.rest.notes.repositories.UserRepository;
import com.rest.notes.services.NotesService;
import com.rest.notes.services.UsersService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.test.context.junit.jupiter.SpringExtension;

import java.util.Arrays;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.mockito.Mockito.*;

@ExtendWith(SpringExtension.class)
public class UsersServiceUnitTest {

    @Mock
    UserRepository userRepository;

    @InjectMocks
    UsersService usersService;

    private User user;

    @BeforeEach
    public void setup() {
        user = new User("test@test.com", "test1234");
    }

    @Test
    public void testGetUserByEmail() {
        when(userRepository.findByEmail("test@test.com")).thenReturn(Optional.of(user));
        UserDetails user = usersService.loadUserByUsername("test@test.com");
        assertEquals(user.getUsername(), "test@test.com");
    }
}

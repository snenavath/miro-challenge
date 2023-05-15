package com.rest.notes;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.rest.notes.models.Note;
import com.rest.notes.models.User;
import com.rest.notes.repositories.NotesRepository;
import com.rest.notes.repositories.UserRepository;
import com.rest.notes.services.NotesService;
import com.rest.notes.services.UsersService;
import org.junit.jupiter.api.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.web.client.TestRestTemplate;
import org.springframework.boot.test.web.server.LocalServerPort;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.http.*;
import org.springframework.test.context.jdbc.Sql;

import java.util.List;
import java.util.Objects;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;


@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@TestInstance(TestInstance.Lifecycle.PER_CLASS)
public class NotesApiIntegrationTest {

    @LocalServerPort
    private int port;

    @Autowired
    private TestRestTemplate restTemplate;

    @Autowired
    private NotesRepository notesRepository;

    @Autowired
    private UserRepository userRepository;

    private static User user;
    private static Note note1;
    private static Note note2;
    private static HttpHeaders headers;

    private final ObjectMapper objectMapper = new ObjectMapper();

    @BeforeAll
    public void init() {
        headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);
        user = new User(1L,"test@test.com", "test1234");
        note1 = new Note(1L,"todo1", "wash car", "test", false, user);
        note2 = new Note(2L,"todo2", "rent house", "test", false, user);
        userRepository.save(user);
        notesRepository.save(note1);
        notesRepository.save(note2);
    }

    @BeforeEach
    public void setUp() throws JsonProcessingException {
//        notesRepository.deleteAll();
//        userRepository.deleteAll();

        headers.setBasicAuth("test@test.com", "test1234");

        HttpEntity<String> userentity = new HttpEntity<>(objectMapper.writeValueAsString(user));
//        ResponseEntity<User> response = restTemplate.exchange(
//                "http://localhost:" + port + "/register", HttpMethod.POST, userentity, User.class);
        ResponseEntity<User> loginresponse = restTemplate.exchange(
                "http://localhost:" + port + "/login", HttpMethod.POST, userentity, User.class);
    }

    @AfterEach
    public void tearDown() {
//        notesRepository.deleteAll();
//        userRepository.deleteAll();
    }
    private String createURLWithPort() {
        return "http://localhost:" + port + "/api/notes";
    }

    @Test
    public void testGetNotes() {
        HttpEntity<String> entity = new HttpEntity<>(null, headers);
        ResponseEntity<List<Note>> response = restTemplate.exchange(
                createURLWithPort(), HttpMethod.GET, entity, new ParameterizedTypeReference<List<Note>>(){});

        List<Note> notes = response.getBody();
        assert notes != null;
        assertEquals(response.getStatusCodeValue(), 200);
        assertEquals(notes.size(), 2);
    }

    @Test
    public void testNotesById() throws JsonProcessingException {

        HttpEntity<String> entity = new HttpEntity<>(null, headers);
        ResponseEntity<Note> response = restTemplate.exchange(
                (createURLWithPort() + "/1"), HttpMethod.GET, entity, Note.class);
        Note note = response.getBody();
        assertEquals(response.getStatusCodeValue(), 200);
        assert note != null;
        assertEquals(note.getTitle(), "todo1");
    }

    @Test
    public void testCreateNote() throws JsonProcessingException {
        Note note = new Note("todo3", "task to create", "test", false, user);

        HttpEntity<String> entity = new HttpEntity<>(objectMapper.writeValueAsString(note), headers);
        ResponseEntity<Note> response = restTemplate.exchange(
                createURLWithPort(), HttpMethod.POST, entity, Note.class);
        assertEquals(response.getStatusCodeValue(), 201);
        Note noteRes = Objects.requireNonNull(response.getBody());
        assertEquals(noteRes.getTitle(), "todo3");
    }
    

}

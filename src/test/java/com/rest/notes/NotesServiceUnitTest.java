package com.rest.notes;

import com.rest.notes.exceptions.NoteNotFoundException;
import com.rest.notes.models.Note;
import com.rest.notes.models.User;
import com.rest.notes.repositories.NotesRepository;
import com.rest.notes.services.NotesService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.springframework.test.context.junit.jupiter.SpringExtension;

import java.util.Arrays;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(SpringExtension.class)
public class NotesServiceUnitTest {

    @Mock
    NotesRepository notesRepository;

    @InjectMocks
    NotesService notesService;

    private User user;
    private Note note1;
    private Note note2;

    @BeforeEach
    public void setup() {
        user = new User("test@test.com", "test1234");
        note1 = new Note(5L,"todo1", "wash car", "test", false, user);
        note2 = new Note(6L,"todo2", "rent house", "test", false, user);
    }

    @Test
    public void testGetNotes() {
        when(notesRepository.findByUser(user.getEmail())).thenReturn(Arrays.asList(note1, note2));
        List<Note> notes = notesService.getNotes(user);
        assertEquals(notes.size(), 2);
        assertEquals(notes.get(0).getTitle(), "todo1");
        assertEquals(notes.get(1).getTitle(), "todo2");
    }

    @Test
    public void testGetNoteById() {
        when(notesRepository.findById(5L)).thenReturn(Optional.of(note1));
        Note note = notesService.getNoteById(5L);
        assertEquals(note.getTitle(), "todo1");
    }

    @Test
    public void testCreateNote() {
        notesService.createNote(note1);
        verify(notesRepository, times(1)).save(note1);
        ArgumentCaptor<Note> orderArgumentCaptor = ArgumentCaptor.forClass(Note.class);
        verify(notesRepository).save(orderArgumentCaptor.capture());
        Note noteCreated = orderArgumentCaptor.getValue();
        assertNotNull(noteCreated.getId());
        assertEquals("todo1", noteCreated.getTitle());
    }

    @Test
    public void testUpdateNote() {
        notesService.createNote(note1);
        verify(notesRepository, times(1)).save(note1);
        note1.setDescription("park car in garage");
        notesService.updateNoteById(5L, note1);
        verify(notesRepository, times(1)).save(note1);
        ArgumentCaptor<Note> orderArgumentCaptor = ArgumentCaptor.forClass(Note.class);
        verify(notesRepository).save(orderArgumentCaptor.capture());
        Note noteUpdated = orderArgumentCaptor.getValue();
        assertNotNull(noteUpdated.getId());
        assertEquals("park car in garage", noteUpdated.getDescription());
    }

    @Test
    public void testSearchNotes() {
        when(notesRepository.search("house")).thenReturn(Arrays.asList(note2));
        List<Note> notes = notesService.searchNotes("house");
        assertEquals(notes.size(), 1);
        assertEquals(notes.get(0).getDescription(), "rent house");
    }

    @Test
    public void testDeleteNote() {
        Note note = new Note(1L, "todo", "note to delete", "test", false, user);
        when(notesRepository.findById(1L)).thenReturn(Optional.of(note));
        notesService.deleteNoteById(note.getId());
        verify(notesRepository, times(1)).deleteById(note.getId());
        ArgumentCaptor<Long> orderArgumentCaptor = ArgumentCaptor.forClass(Long.class);
        verify(notesRepository).deleteById(orderArgumentCaptor.capture());
        Long NoteIdDeleted = orderArgumentCaptor.getValue();
        assertNotNull(NoteIdDeleted);
        assertEquals(1L, NoteIdDeleted);
    }
}
